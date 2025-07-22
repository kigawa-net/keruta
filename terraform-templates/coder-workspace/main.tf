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
    namespace = data.coder_provisioner.me.tags["namespace"] != "" ? data.coder_provisioner.me.tags["namespace"] : "default"
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
    key   = "Mount Path"
    value = "/home/coder/shared"
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