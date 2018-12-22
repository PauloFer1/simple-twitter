# Stop any container that could be running
docker stop $(docker ps -q --filter ancestor=hsbc/simple-twitter)
#Run container
docker run -d -p 8080:8080 -t hsbc/simple-twitter