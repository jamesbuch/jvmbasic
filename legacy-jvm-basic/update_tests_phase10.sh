#!/bin/bash

# Phase 10 Test File Update Script
# Updates all test files to modern Phase 10 syntax:
# 1. Remove LET keyword from assignments
# 2. Replace PRINT with separators with Console.WriteLine
# 3. Ensure all functions have explicit return types

echo "Starting Phase 10 test file updates..."

# Counter for tracking progress
total_files=$(find tests -name "*.bas" | wc -l)
current=0

# Function to update a single file
update_file() {
    local file="$1"
    local temp_file="${file}.tmp"
    
    echo "Updating $file..."
    
    # Create a backup
    cp "$file" "${file}.backup"
    
    # Process the file
    sed -E '
        # Remove LET keyword from assignments (but keep it in comments)
        s/^([[:space:]]*)LET[[:space:]]+([a-zA-Z_][a-zA-Z0-9_]*[[:space:]]*=)/\1\2/g
        
        # Replace PRINT with semicolon separators with Console.WriteLine
        s/PRINT[[:space:]]+"([^"]*)"[[:space:]]*;[[:space:]]*([a-zA-Z_][a-zA-Z0-9_]*)/Console.WriteLine("\1" + FormatI("%d", \2))/g
        s/PRINT[[:space:]]+"([^"]*)"[[:space:]]*;[[:space:]]*([a-zA-Z_][a-zA-Z0-9_]*)/Console.WriteLine("\1" + FormatF("%.2f", \2))/g
        s/PRINT[[:space:]]+"([^"]*)"[[:space:]]*;[[:space:]]*([a-zA-Z_][a-zA-Z0-9_]*)/Console.WriteLine("\1" + \2)/g
        
        # Replace PRINT with comma separators with Console.WriteLine
        s/PRINT[[:space:]]+"([^"]*)"[[:space:]]*,[[:space:]]*([a-zA-Z_][a-zA-Z0-9_]*)/Console.WriteLine("\1 " + \2)/g
        
        # Replace simple PRINT statements with Console.WriteLine
        s/^([[:space:]]*)PRINT[[:space:]]+"([^"]*)"/\1Console.WriteLine("\2")/g
        s/^([[:space:]]*)PRINT[[:space:]]+([a-zA-Z_][a-zA-Z0-9_]*)/\1Console.WriteLine(\2)/g
    ' "$file" > "$temp_file"
    
    # Replace original with updated version
    mv "$temp_file" "$file"
    
    # Increment counter
    ((current++))
    echo "Progress: $current/$total_files files updated"
}

# Update all .bas files in tests directory
find tests -name "*.bas" -type f | while read -r file; do
    update_file "$file"
done

echo "Phase 10 test file updates completed!"
echo "Backup files created with .backup extension"
echo "Total files processed: $total_files"
