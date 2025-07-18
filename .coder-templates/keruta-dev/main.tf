terraform {
  required_providers {
    coder = {
      source = "coder/coder"
    }
    kubernetes = {
      source = "hashicorp/kubernetes"
    }
  }
}

variable "use_kubeconfig" {
  type        = bool
  description = <<-EOF
  Use host kubeconfig? (true/false)
  
  Set this to false if the Coder host is itself running as a Pod on the same
  Kubernetes cluster as you are deploying workspaces to.
  
  Set this to true if the Coder host is running outside the Kubernetes cluster
  and you want to use the host's kubeconfig to provision workspaces.
  EOF
  default     = false
}

variable "namespace" {
  type        = string
  description = "The Kubernetes namespace to create workspaces in (must exist prior to creating workspaces)"
  default     = "coder-workspaces"
}

variable "storage_class" {
  type        = string
  description = "The Kubernetes storage class to use for workspaces"
  default     = "local-path"
}

variable "cpu_limit" {
  type        = string
  description = "CPU limit for the workspace"
  default     = "2"
}

variable "memory_limit" {
  type        = string
  description = "Memory limit for the workspace"
  default     = "4Gi"
}

variable "cpu_request" {
  type        = string
  description = "CPU request for the workspace"
  default     = "500m"
}

variable "memory_request" {
  type        = string
  description = "Memory request for the workspace"
  default     = "1Gi"
}

variable "home_disk_size" {
  type        = number
  description = "Size of the home disk in GB"
  default     = 10
}

provider "kubernetes" {
  config_path = var.use_kubeconfig ? "~/.kube/config" : null
}

data "coder_workspace" "me" {
}

data "coder_workspace_owner" "me" {
}

resource "coder_agent" "main" {
  arch                   = "amd64"
  os                     = "linux"
  startup_script_timeout = 180
  startup_script         = <<-EOT
    set -e
    
    # Wait for Docker to be available
    while ! docker info > /dev/null 2>&1; do
      echo "Waiting for Docker to be available..."
      sleep 5
    done
    
    # Clone the repository if it doesn't exist
    if [ ! -d "/home/coder/workspace/keruta" ]; then
      echo "Cloning keruta repository..."
      git clone https://github.com/kigawa-net/keruta.git /home/coder/workspace/keruta
    fi
    
    # Set up the environment
    cd /home/coder/workspace/keruta
    
    # Make gradlew executable
    chmod +x gradlew
    
    echo "Development environment is ready!"
  EOT

  # The following metadata blocks are optional. They are used to display
  # information about your workspace in the dashboard. You can remove them
  # if you don't want to display any information.
  metadata {
    display_name = "CPU Usage"
    key          = "0_cpu_usage"
    script       = "coder stat cpu"
    interval     = 10
    timeout      = 1
  }

  metadata {
    display_name = "RAM Usage"
    key          = "1_ram_usage"
    script       = "coder stat mem"
    interval     = 10
    timeout      = 1
  }

  metadata {
    display_name = "Home Disk"
    key          = "3_home_disk"
    script       = "coder stat disk --path $HOME"
    interval     = 60
    timeout      = 1
  }

  metadata {
    display_name = "CPU Usage (Host)"
    key          = "4_cpu_usage_host"
    script       = "coder stat cpu --host"
    interval     = 10
    timeout      = 1
  }

  metadata {
    display_name = "Memory Usage (Host)"
    key          = "5_mem_usage_host"
    script       = "coder stat mem --host"
    interval     = 10
    timeout      = 1
  }

  metadata {
    display_name = "Load Average (Host)"
    key          = "6_load_host"
    script       = "uptime | awk -F'load average:' '{ print $2 }'"
    interval     = 60
    timeout      = 1
  }
}

resource "coder_app" "code-server" {
  agent_id     = coder_agent.main.id
  slug         = "code-server"
  display_name = "code-server"
  url          = "http://localhost:8080/?folder=/home/coder/workspace/keruta"
  icon         = "/icon/code.svg"
  subdomain    = false
  share        = "owner"

  healthcheck {
    url       = "http://localhost:8080/healthz"
    interval  = 5
    threshold = 6
  }
}

resource "coder_app" "keruta-api" {
  agent_id     = coder_agent.main.id
  slug         = "keruta-api"
  display_name = "Keruta API"
  url          = "http://localhost:8080"
  icon         = "/icon/swagger.svg"
  subdomain    = false
  share        = "owner"

  healthcheck {
    url       = "http://localhost:8080/api/health"
    interval  = 30
    threshold = 3
  }
}

resource "coder_app" "keruta-admin" {
  agent_id     = coder_agent.main.id
  slug         = "keruta-admin"
  display_name = "Keruta Admin"
  url          = "http://localhost:8080/admin"
  icon         = "/icon/dashboard.svg"
  subdomain    = false
  share        = "owner"

  healthcheck {
    url       = "http://localhost:8080/admin"
    interval  = 30
    threshold = 3
  }
}

resource "kubernetes_persistent_volume_claim" "home" {
  metadata {
    name      = "coder-${data.coder_workspace_owner.me.name}-${data.coder_workspace.me.name}-home"
    namespace = var.namespace
    labels = {
      "app.kubernetes.io/name"     = "coder-pvc"
      "app.kubernetes.io/instance" = "coder-pvc-${data.coder_workspace_owner.me.name}-${data.coder_workspace.me.name}"
      "app.kubernetes.io/part-of"  = "coder"
      "coder.owner"                = data.coder_workspace_owner.me.name
      "coder.workspace"            = data.coder_workspace.me.name
    }
  }
  wait_until_bound = false
  spec {
    access_modes = ["ReadWriteOnce"]
    resources {
      requests = {
        storage = "${var.home_disk_size}Gi"
      }
    }
    storage_class_name = var.storage_class
  }
}

resource "kubernetes_deployment" "main" {
  count = data.coder_workspace.me.start_count
  depends_on = [
    kubernetes_persistent_volume_claim.home
  ]
  wait_for_rollout = false
  metadata {
    name      = "coder-${data.coder_workspace_owner.me.name}-${data.coder_workspace.me.name}"
    namespace = var.namespace
    labels = {
      "app.kubernetes.io/name"     = "coder-workspace"
      "app.kubernetes.io/instance" = "coder-workspace-${data.coder_workspace_owner.me.name}-${data.coder_workspace.me.name}"
      "app.kubernetes.io/part-of"  = "coder"
      "coder.owner"                = data.coder_workspace_owner.me.name
      "coder.workspace"            = data.coder_workspace.me.name
    }
  }

  spec {
    replicas = 1
    strategy {
      type = "Recreate"
    }

    selector {
      match_labels = {
        "app.kubernetes.io/name"     = "coder-workspace"
        "app.kubernetes.io/instance" = "coder-workspace-${data.coder_workspace_owner.me.name}-${data.coder_workspace.me.name}"
      }
    }

    template {
      metadata {
        labels = {
          "app.kubernetes.io/name"     = "coder-workspace"
          "app.kubernetes.io/instance" = "coder-workspace-${data.coder_workspace_owner.me.name}-${data.coder_workspace.me.name}"
          "app.kubernetes.io/part-of"  = "coder"
          "coder.owner"                = data.coder_workspace_owner.me.name
          "coder.workspace"            = data.coder_workspace.me.name
        }
      }

      spec {
        security_context {
          run_as_user = "1000"
          fs_group    = "1000"
        }

        container {
          name  = "dev"
          image = "kigawa/keruta-dev:latest"
          command = ["sh", "-c", coder_agent.main.init_script]

          security_context {
            run_as_user = "1000"
          }

          env {
            name  = "CODER_AGENT_TOKEN"
            value = coder_agent.main.token
          }

          env {
            name  = "JAVA_HOME"
            value = "/usr/lib/jvm/java-21-openjdk-amd64"
          }

          env {
            name  = "GOROOT"
            value = "/usr/local/go"
          }

          env {
            name  = "GOPATH"
            value = "/home/coder/go"
          }

          env {
            name  = "PATH"
            value = "/usr/local/go/bin:/usr/lib/jvm/java-21-openjdk-amd64/bin:/home/coder/go/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
          }

          resources {
            requests = {
              "cpu"    = var.cpu_request
              "memory" = var.memory_request
            }
            limits = {
              "cpu"    = var.cpu_limit
              "memory" = var.memory_limit
            }
          }

          volume_mount {
            mount_path = "/home/coder"
            name       = "home"
            read_only  = false
          }

          volume_mount {
            mount_path = "/var/run/docker.sock"
            name       = "docker-sock"
            read_only  = false
          }
        }

        volume {
          name = "home"
          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.home.metadata.0.name
            read_only  = false
          }
        }

        volume {
          name = "docker-sock"
          host_path {
            path = "/var/run/docker.sock"
            type = "Socket"
          }
        }

        affinity {
          pod_anti_affinity {
            preferred_during_scheduling_ignored_during_execution {
              weight = 1
              pod_affinity_term {
                topology_key = "kubernetes.io/hostname"
                label_selector {
                  match_expressions {
                    key      = "app.kubernetes.io/name"
                    operator = "In"
                    values   = ["coder-workspace"]
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "main" {
  count = data.coder_workspace.me.start_count
  metadata {
    name      = "coder-${data.coder_workspace_owner.me.name}-${data.coder_workspace.me.name}"
    namespace = var.namespace
    labels = {
      "app.kubernetes.io/name"     = "coder-workspace"
      "app.kubernetes.io/instance" = "coder-workspace-${data.coder_workspace_owner.me.name}-${data.coder_workspace.me.name}"
      "app.kubernetes.io/part-of"  = "coder"
      "coder.owner"                = data.coder_workspace_owner.me.name
      "coder.workspace"            = data.coder_workspace.me.name
    }
  }
  spec {
    selector = {
      "app.kubernetes.io/name"     = "coder-workspace"
      "app.kubernetes.io/instance" = "coder-workspace-${data.coder_workspace_owner.me.name}-${data.coder_workspace.me.name}"
    }
    port {
      name        = "code-server"
      port        = 8080
      target_port = 8080
      protocol    = "TCP"
    }
  }
}