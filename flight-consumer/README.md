# flight-consumer

## Requirements 

Install and launch the MongoDB database.

Install (mac):
```bash
$ brew tap mongodb/brew
$ brew install mongodb-community@4.2
```

To run MongoDB (i.e. the mongod process) as a macOS service:
```bash
$ brew services start mongodb-community
```

Verify it is running:
```bash
$ ps aux | grep -v grep | grep mongod
```
