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

locals {
  username = data.coder_workspace.me.owner
}

data "coder_provisioner" "me" {
}

data "coder_workspace" "me" {
}

# Create a PVC for persistent user data
resource "kubernetes_persistent_volume_claim" "user_pvc" {
  metadata {
    name      = "coder-${lower(local.username)}-shared"
    namespace = try(data.coder_provisioner.me.tags["namespace"], "default")
  }
  wait_until_bound = false
  spec {
    access_modes = ["ReadWriteMany"]
    resources {
      requests = {
        storage = "10Gi"
      }
    }
    storage_class_name = var.storage_class_name
  }
}

# Display persistent storage info in Coder UI
resource "coder_metadata" "pvc_info" {
  count       = data.coder_workspace.me.start_count
  resource_id = kubernetes_persistent_volume_claim.user_pvc.id

  item {
    key   = "Volume Name"
    value = kubernetes_persistent_volume_claim.user_pvc.metadata[0].name
  }

  item {
    key   = "Size"
    value = kubernetes_persistent_volume_claim.user_pvc.spec[0].resources[0].requests.storage
  }

  item {
    key   = "Storage Class"
    value = kubernetes_persistent_volume_claim.user_pvc.spec[0].storage_class_name
  }

  item {
    key   = "Access Mode"
    value = "ReadWriteMany"
  }

  item {
    key       = "Mount Path"
    value     = "/home/coder/shared"
    sensitive = false
  }
}

# Define storage class variable
variable "storage_class_name" {
  description = "The storage class to use for persistent volumes"
  type        = string
  default     = "standard"
  validation {
    condition     = var.storage_class_name != ""
    error_message = "Storage class name must not be empty."
  }
}

# Output values for use in workspace templates
output "pvc_name" {
  description = "The name of the created PVC"
  value       = kubernetes_persistent_volume_claim.user_pvc.metadata[0].name
}

output "mount_path" {
  description = "The path where the PVC should be mounted"
  value       = "/home/coder/shared"
}

# Claude Code configuration parameters
variable "claude_code_enabled" {
  description = "Enable Claude Code installation in the workspace"
  type        = bool
  default     = true
}

variable "claude_code_api_key" {
  description = "Anthropic API key for Claude Code (optional - can be set by user later)"
  type        = string
  default     = ""
  sensitive   = true
}

variable "node_version" {
  description = "Node.js version to install for Claude Code"
  type        = string
  default     = "20"
}

# Keruta Agent configuration parameters
variable "keruta_agent_enabled" {
  description = "Enable keruta-agent daemon in the workspace"
  type        = bool
  default     = true
}

variable "keruta_agent_url" {
  description = "URL to download keruta-agent binary"
  type        = string
  default     = "https://releases.keruta.net"
}

variable "keruta_agent_version" {
  description = "Version of keruta-agent to install"
  type        = string
  default     = "latest"
}

variable "keruta_api_url" {
  description = "URL of the Keruta API server"
  type        = string
  default     = "http://keruta-api:8080"
}

variable "keruta_api_token" {
  description = "API token for Keruta API authentication (optional)"
  type        = string
  default     = ""
  sensitive   = true
}

# Create a Coder agent for the workspace
resource "coder_agent" "main" {
  arch                   = try(data.coder_provisioner.me.tags["arch"], "amd64")
  os                     = "linux"
  startup_script_timeout = 300
  startup_script = templatefile("${path.module}/startup.sh", {
    node_version         = var.node_version
    claude_api_key       = var.claude_code_api_key
    keruta_agent_enabled = var.keruta_agent_enabled
    keruta_agent_url     = var.keruta_agent_url
    keruta_agent_version = var.keruta_agent_version
    keruta_api_url       = var.keruta_api_url
    keruta_api_token     = var.keruta_api_token
  })

  # Claude Code application in Coder UI
  dynamic "app" {
    for_each = var.claude_code_enabled ? [1] : []
    content {
      slug         = "claude-code"
      display_name = "Claude Code"
      icon         = "https://www.anthropic.com/favicon.ico"
      url          = "https://claude.ai/code"
      subdomain    = false
      share        = "owner"
      healthcheck {
        url       = "http://localhost:3000/health"
        interval  = 10
        threshold = 3
      }
    }
  }

  metadata {
    key   = "claude_code_enabled"
    value = var.claude_code_enabled ? "true" : "false"
  }

  metadata {
    key   = "node_version"
    value = var.node_version
  }

  metadata {
    key   = "keruta_agent_enabled"
    value = var.keruta_agent_enabled ? "true" : "false"
  }
}

# Kubernetes deployment for the workspace
resource "kubernetes_deployment" "workspace" {
  count = data.coder_workspace.me.start_count

  metadata {
    name      = "coder-${lower(local.username)}"
    namespace = try(data.coder_provisioner.me.tags["namespace"], "default")
    labels = {
      "app.kubernetes.io/name"     = "coder-workspace"
      "app.kubernetes.io/instance" = data.coder_workspace.me.name
      "app.kubernetes.io/part-of"  = "coder"
      "com.coder.resource"         = "true"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        "app.kubernetes.io/name"     = "coder-workspace"
        "app.kubernetes.io/instance" = data.coder_workspace.me.name
      }
    }

    template {
      metadata {
        labels = {
          "app.kubernetes.io/name"     = "coder-workspace"
          "app.kubernetes.io/instance" = data.coder_workspace.me.name
        }
      }

      spec {
        security_context {
          run_as_user = 1000
          fs_group    = 1000
        }

        container {
          name              = "workspace"
          image             = "codercom/enterprise-base:ubuntu"
          image_pull_policy = "Always"

          command = ["/bin/bash", "-c", coder_agent.main.init_script]

          env {
            name  = "CODER_AGENT_TOKEN"
            value = coder_agent.main.token
          }

          env {
            name  = "CODER_WORKSPACE_ID"
            value = data.coder_workspace.me.id
          }

          # Add Claude Code environment variables
          dynamic "env" {
            for_each = var.claude_code_enabled && var.claude_code_api_key != "" ? [1] : []
            content {
              name  = "ANTHROPIC_API_KEY"
              value = var.claude_code_api_key
            }
          }

          # Add Keruta Agent environment variables
          dynamic "env" {
            for_each = var.keruta_agent_enabled ? [1] : []
            content {
              name  = "KERUTA_API_URL"
              value = var.keruta_api_url
            }
          }

          dynamic "env" {
            for_each = var.keruta_agent_enabled && var.keruta_api_token != "" ? [1] : []
            content {
              name  = "KERUTA_API_TOKEN"
              value = var.keruta_api_token
            }
          }

          resources {
            requests = {
              cpu    = "250m"
              memory = "512Mi"
            }
            limits = {
              cpu    = "2"
              memory = "4Gi"
            }
          }

          volume_mount {
            mount_path = "/home/coder/shared"
            name       = "shared-storage"
          }

          volume_mount {
            mount_path = "/home/coder"
            name       = "home-storage"
          }
        }

        volume {
          name = "shared-storage"
          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.user_pvc.metadata[0].name
          }
        }

        volume {
          name = "home-storage"
          empty_dir {
            size_limit = "10Gi"
          }
        }
      }
    }
  }
}

# Service for workspace (if needed for Claude Code web interface)
resource "kubernetes_service" "workspace" {
  count = data.coder_workspace.me.start_count

  metadata {
    name      = "coder-${lower(local.username)}"
    namespace = try(data.coder_provisioner.me.tags["namespace"], "default")
  }

  spec {
    selector = {
      "app.kubernetes.io/name"     = "coder-workspace"
      "app.kubernetes.io/instance" = data.coder_workspace.me.name
    }

    port {
      name        = "claude-code"
      port        = 3000
      target_port = 3000
      protocol    = "TCP"
    }

    type = "ClusterIP"
  }
}

# Output Claude Code information
output "claude_code_enabled" {
  description = "Whether Claude Code is enabled in this workspace"
  value       = var.claude_code_enabled
}

output "claude_code_command" {
  description = "Command to run Claude Code in the terminal"
  value       = var.claude_code_enabled ? "claude-code" : "Claude Code not enabled"
}