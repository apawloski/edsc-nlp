echo "Downloading index.."
aws s3 cp s3://gsfc-eosdis-ngap-us-east-1/prod/app/edsc-clavin/clavin-index.tar.gz .
echo "Decompressing index.."
tar xzf clavin-index.tar.gz
rm clavin-index.tar.gz
echo "Index ready!"
