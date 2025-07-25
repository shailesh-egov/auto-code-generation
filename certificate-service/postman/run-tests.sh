#!/bin/bash

# Certificate Service API Test Runner
# This script runs the Postman collection using Newman CLI

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
COLLECTION_FILE="Certificate-Service.postman_collection.json"
LOCAL_ENV_FILE="Certificate-Service-Local.postman_environment.json"
DEV_ENV_FILE="Certificate-Service-Dev.postman_environment.json"
REPORT_DIR="test-reports"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Function to check if Newman is installed
check_newman() {
    if ! command -v newman &> /dev/null; then
        print_error "Newman CLI is not installed"
        print_status "Installing Newman globally..."
        npm install -g newman
        npm install -g newman-reporter-html
    else
        print_success "Newman CLI is available"
    fi
}

# Function to check if service is running
check_service() {
    local url=$1
    print_status "Checking if service is available at $url"
    
    if curl -s -f "$url/health" > /dev/null 2>&1; then
        print_success "Service is running and healthy"
        return 0
    else
        print_warning "Service health check failed, but continuing with tests"
        return 1
    fi
}

# Function to run tests
run_tests() {
    local environment=$1
    local env_file=$2
    
    print_status "Running tests for $environment environment"
    
    # Create reports directory
    mkdir -p "$REPORT_DIR"
    
    # Generate timestamp for report
    local timestamp=$(date +"%Y%m%d_%H%M%S")
    local report_file="$REPORT_DIR/certificate-service-test-report-$environment-$timestamp"
    
    # Run Newman with collection and environment
    newman run "$COLLECTION_FILE" \
        -e "$env_file" \
        --reporters cli,html \
        --reporter-html-export "$report_file.html" \
        --bail \
        --timeout-request 30000 \
        --timeout-script 5000 \
        --color on
    
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        print_success "All tests passed for $environment environment"
        print_status "HTML report generated: $report_file.html"
    else
        print_error "Some tests failed for $environment environment"
        print_status "Check the HTML report for details: $report_file.html"
    fi
    
    return $exit_code
}

# Function to display help
show_help() {
    echo "Certificate Service API Test Runner"
    echo ""
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -e, --environment ENV    Run tests for specific environment (local|dev|all)"
    echo "  -h, --help              Show this help message"
    echo "  -v, --verbose           Enable verbose output"
    echo "  --skip-health-check     Skip service health check"
    echo ""
    echo "Examples:"
    echo "  $0                      # Run tests for local environment"
    echo "  $0 -e dev               # Run tests for dev environment"
    echo "  $0 -e all               # Run tests for all environments"
    echo "  $0 --skip-health-check  # Skip health check and run tests"
    echo ""
}

# Main function
main() {
    local environment="local"
    local skip_health_check=false
    local verbose=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -e|--environment)
                environment="$2"
                shift 2
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            -v|--verbose)
                verbose=true
                shift
                ;;
            --skip-health-check)
                skip_health_check=true
                shift
                ;;
            *)
                print_error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    print_status "Certificate Service API Test Runner"
    print_status "===================================="
    
    # Check if collection file exists
    if [ ! -f "$COLLECTION_FILE" ]; then
        print_error "Collection file not found: $COLLECTION_FILE"
        exit 1
    fi
    
    # Check Newman installation
    check_newman
    
    # Run tests based on environment
    case $environment in
        local)
            if [ ! -f "$LOCAL_ENV_FILE" ]; then
                print_error "Local environment file not found: $LOCAL_ENV_FILE"
                exit 1
            fi
            
            if [ "$skip_health_check" = false ]; then
                check_service "http://localhost:8039/certificate-service"
            fi
            
            run_tests "local" "$LOCAL_ENV_FILE"
            ;;
        dev)
            if [ ! -f "$DEV_ENV_FILE" ]; then
                print_error "Dev environment file not found: $DEV_ENV_FILE"
                exit 1
            fi
            
            if [ "$skip_health_check" = false ]; then
                check_service "https://works-dev.digit.org/certificate-service"
            fi
            
            run_tests "dev" "$DEV_ENV_FILE"
            ;;
        all)
            print_status "Running tests for all environments"
            
            # Run local tests
            if [ -f "$LOCAL_ENV_FILE" ]; then
                if [ "$skip_health_check" = false ]; then
                    check_service "http://localhost:8039/certificate-service"
                fi
                run_tests "local" "$LOCAL_ENV_FILE"
                local_result=$?
            else
                print_warning "Local environment file not found, skipping local tests"
                local_result=0
            fi
            
            echo ""
            
            # Run dev tests
            if [ -f "$DEV_ENV_FILE" ]; then
                if [ "$skip_health_check" = false ]; then
                    check_service "https://works-dev.digit.org/certificate-service"
                fi
                run_tests "dev" "$DEV_ENV_FILE"
                dev_result=$?
            else
                print_warning "Dev environment file not found, skipping dev tests"
                dev_result=0
            fi
            
            # Overall result
            if [ $local_result -eq 0 ] && [ $dev_result -eq 0 ]; then
                print_success "All tests passed for all environments"
                exit 0
            else
                print_error "Some tests failed"
                exit 1
            fi
            ;;
        *)
            print_error "Invalid environment: $environment"
            print_status "Valid environments: local, dev, all"
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"