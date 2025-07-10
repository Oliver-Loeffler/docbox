#!/bin/bash

# Usage: ./copy_if_not_exists.sh /path/to/source /path/to/target

SOURCE_DIR="/var/docbox/dist"
TARGET_DIR="/var/www/html/dist"

# Check for correct usage
if [[ -z "$SOURCE_DIR" || -z "$TARGET_DIR" ]]; then
  echo "Usage: $0 /path/to/source /path/to/target"
  exit 1
fi

# Check if target directory exists
if [[ -d "$TARGET_DIR" ]]; then
  echo "Target directory '$TARGET_DIR' already exists. Nothing to do."
else
  echo "Copying '$SOURCE_DIR' to '$TARGET_DIR'..."
  cp -r "$SOURCE_DIR" "$TARGET_DIR"
  echo "Copy complete."
fi
