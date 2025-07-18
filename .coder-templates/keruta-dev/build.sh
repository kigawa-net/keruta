#!/bin/bash

# Keruta Development Template Build Script

set -e

TEMPLATE_NAME="keruta-dev"
IMAGE_NAME="kigawa/keruta-dev"
TAG="latest"

echo "=== Building Keruta Development Template ==="

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Building Docker image: $IMAGE_NAME:$TAG"

# Build the Docker image
docker build -t "$IMAGE_NAME:$TAG" .

echo "Docker image built successfully: $IMAGE_NAME:$TAG"

# Optional: Push to registry
if [ "$1" = "push" ]; then
    echo "Pushing image to registry..."
    docker push "$IMAGE_NAME:$TAG"
    echo "Image pushed successfully"
fi

echo "=== Build Complete ==="
echo "To use this template:"
echo "1. Make sure the image is available in your registry"
echo "2. Create a new template in Coder using main.tf"
echo "3. Create workspaces from the template"