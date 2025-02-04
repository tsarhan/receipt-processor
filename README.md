# Receipt Processor
Web service for awarding points to submitted receipts.

# Run Steps
1. Clone or download repo
2. Docker Desktop/CLI is required to run this so please make sure to have it installed locally 
3. In root directory of the project build the docker image with the command below. 
```
docker build -t receipt-processor .
```
4. Run with this command
```
docker run -p 8080:8080 -t receipt-processor
```
5. Have fun testing