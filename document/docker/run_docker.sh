#!/bin/bash

# Function to run a command in a new terminal window
run_in_terminal() {
    local command="$1"
    gnome-terminal -- bash -c "$command; read -p 'Press Enter to close...'"
}

# Run the first docker-compose command
run_in_terminal "echo 123 | sudo -S docker-compose -f docker-compose-env.yml up"

# Wait for 3 minutes
sleep 180

# Run the second docker-compose command
run_in_terminal "echo 123 | sudo -S docker-compose -f docker-compose-app.yml up"
