echo "Downloading and decompressing index.."
aws s3 cp s3://gsfc-eosdis-ngap-us-east-1/prod/app/edsc-nlp/clavin-index.tar.gz - | tar xzf -
echo "Index ready!"

