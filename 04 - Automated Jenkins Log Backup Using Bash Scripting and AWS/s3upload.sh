#!/bin/bash

#
# Author: Yuvraj.R
# This script uploads today's Jenkins build logs to an S3 bucket.
#

# Location where Jenkins saves its files
JENKINS_HOME="/var/lib/jenkins"

# Name of the S3 bucket where we want to save the logs
S3_BUCKET="s3://jenkins-cost-optimization21"

# Get today's date
DATE=$(date +%Y-%m-%d)

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "AWS CLI is not installed. Please install it first."
    exit 1
fi

# Go through each Jenkins job folder
for job_dir in "$JENKINS_HOME/jobs/"*/; do

    # Get the job name from the folder name
    job_name=$(basename "$job_dir")

    # Go through each build folder inside this job
    for build_dir in "$job_dir/builds/"*/; do

        # Get the build number
        build_number=$(basename "$build_dir")

        # Find the log file inside this build folder
        log_file="$build_dir/log"

        # Check if the log file exists and was changed today
        if [ -f "$log_file" ] && [ "$(date -r "$log_file" +%Y-%m-%d)" == "$DATE" ]; then

            # Upload the log file to the S3 bucket
            # Give the uploaded file the job name and build number
            aws s3 cp "$log_file" "$S3_BUCKET/$job_name-$build_number.log" --only-show-errors

            # Check if the upload worked
            if [ $? -eq 0 ]; then

                # Show a success message
                echo "Uploaded: $job_name/$build_number to $S3_BUCKET/$job_name-$build_number.log"
            else

                # Show an error message if the upload failed
                echo "Failed to upload: $job_name/$build_number"
            fi
        fi
    done
done
