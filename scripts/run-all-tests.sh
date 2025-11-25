#!/bin/bash

#############################################################################
# Run All Tests Script
# 
# This script runs all JUnit tests for the SDV Developer Console platform
# and generates a consolidated test report.
#############################################################################

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SERVICES=(
    "scenario-library-service"
    "webhook-management-service"
    "message-queue-service"
    "tracks-management-service"
)

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
REPORT_DIR="test-reports-${TIMESTAMP}"
mkdir -p "${REPORT_DIR}"

# Function to print colored output
print_color() {
    local color=$1
    shift
    echo -e "${color}$@${NC}"
}

# Function to print section header
print_header() {
    echo ""
    print_color "${BLUE}" "═══════════════════════════════════════════════════════════════"
    print_color "${BLUE}" "  $1"
    print_color "${BLUE}" "═══════════════════════════════════════════════════════════════"
    echo ""
}

# Function to run tests for a service
run_service_tests() {
    local service=$1
    local service_report_dir="${REPORT_DIR}/${service}"
    
    print_header "Testing: ${service}"
    
    if [ ! -d "${service}" ]; then
        print_color "${YELLOW}" "⚠️  Service directory not found: ${service}"
        return 1
    fi
    
    cd "${service}"
    
    # Run Maven tests
    print_color "${YELLOW}" "Running Maven tests..."
    if mvn clean test -B 2>&1 | tee "../${service_report_dir}.log"; then
        print_color "${GREEN}" "✅ Tests PASSED for ${service}"
        
        # Copy test reports
        if [ -d "app/target/surefire-reports" ]; then
            mkdir -p "../${service_report_dir}"
            cp -r app/target/surefire-reports/* "../${service_report_dir}/" 2>/dev/null || true
            print_color "${GREEN}" "📊 Test reports saved to ${service_report_dir}/"
        fi
        
        cd ..
        return 0
    else
        print_color "${RED}" "❌ Tests FAILED for ${service}"
        
        # Copy failure reports
        if [ -d "app/target/surefire-reports" ]; then
            mkdir -p "../${service_report_dir}"
            cp -r app/target/surefire-reports/* "../${service_report_dir}/" 2>/dev/null || true
            print_color "${RED}" "📊 Failure reports saved to ${service_report_dir}/"
        fi
        
        cd ..
        return 1
    fi
}

# Function to generate summary
generate_summary() {
    local total=$1
    local passed=$2
    local failed=$3
    
    print_header "Test Summary"
    
    echo "Total Services Tested: ${total}"
    echo ""
    print_color "${GREEN}" "✅ Passed: ${passed}"
    print_color "${RED}" "❌ Failed: ${failed}"
    echo ""
    
    if [ ${failed} -eq 0 ]; then
        print_color "${GREEN}" "🎉 All tests passed!"
        echo ""
        echo "Test reports saved in: ${REPORT_DIR}/"
        return 0
    else
        print_color "${RED}" "⚠️  Some tests failed. Please review the reports."
        echo ""
        echo "Test reports saved in: ${REPORT_DIR}/"
        echo "Logs saved with extension: .log"
        return 1
    fi
}

# Function to show test statistics
show_statistics() {
    print_header "Test Statistics"
    
    for service in "${SERVICES[@]}"; do
        local service_report_dir="${REPORT_DIR}/${service}"
        
        if [ -d "${service_report_dir}" ]; then
            echo ""
            print_color "${BLUE}" "📊 ${service}:"
            
            # Count test results from XML files
            local test_files=$(find "${service_report_dir}" -name "TEST-*.xml" 2>/dev/null)
            
            if [ -n "${test_files}" ]; then
                local total_tests=0
                local total_failures=0
                local total_errors=0
                local total_skipped=0
                
                while IFS= read -r file; do
                    if command -v xmllint &> /dev/null; then
                        tests=$(xmllint --xpath 'string(/testsuite/@tests)' "$file" 2>/dev/null || echo "0")
                        failures=$(xmllint --xpath 'string(/testsuite/@failures)' "$file" 2>/dev/null || echo "0")
                        errors=$(xmllint --xpath 'string(/testsuite/@errors)' "$file" 2>/dev/null || echo "0")
                        skipped=$(xmllint --xpath 'string(/testsuite/@skipped)' "$file" 2>/dev/null || echo "0")
                        
                        total_tests=$((total_tests + ${tests:-0}))
                        total_failures=$((total_failures + ${failures:-0}))
                        total_errors=$((total_errors + ${errors:-0}))
                        total_skipped=$((total_skipped + ${skipped:-0}))
                    fi
                done <<< "${test_files}"
                
                local passed=$((total_tests - total_failures - total_errors - total_skipped))
                
                echo "   Tests: ${total_tests}"
                print_color "${GREEN}" "   Passed: ${passed}"
                [ ${total_failures} -gt 0 ] && print_color "${RED}" "   Failures: ${total_failures}"
                [ ${total_errors} -gt 0 ] && print_color "${RED}" "   Errors: ${total_errors}"
                [ ${total_skipped} -gt 0 ] && print_color "${YELLOW}" "   Skipped: ${total_skipped}"
            else
                print_color "${YELLOW}" "   No test results found"
            fi
        fi
    done
    
    echo ""
}

# Main execution
main() {
    print_header "SDV Platform - Test Execution"
    
    echo "Starting test execution at: $(date)"
    echo "Report directory: ${REPORT_DIR}"
    echo ""
    
    local total_services=${#SERVICES[@]}
    local passed_services=0
    local failed_services=0
    
    # Run tests for each service
    for service in "${SERVICES[@]}"; do
        if run_service_tests "${service}"; then
            ((passed_services++))
        else
            ((failed_services++))
        fi
    done
    
    # Show detailed statistics
    show_statistics
    
    # Generate and display summary
    generate_summary ${total_services} ${passed_services} ${failed_services}
    
    local exit_code=$?
    
    echo ""
    print_color "${BLUE}" "Test execution completed at: $(date)"
    
    exit ${exit_code}
}

# Run main function
main "$@"
