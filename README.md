# POC for Bootstrap server with UIAutomator 2

Bootstrap server is modified to use the new UIAutomator2 which has fixed many issues which were there with UIAutomator1. Additionally, Bootstrap server now communicates with its clients through HTTP calls instead of socket connection. Communication is through JSON Wire Protocol as before.

### Sample JSON Request
```
{
    "cmd": "action",
    "action": "click",
    "params": {
        "byCategory": "DESC",
        "byValue": "plus"
    }
}
```
* Bootstrap server supports only *POST* calls and mime-type should always be *"application/json"*
* Bootstrap server will keep on listening to HTTP clients till *shutdown* command is passed

```
{
    "cmd" : "shutdown"
}
```

### About this POC
* As this is just a POC project, *click* command alone is suuported. So *action* in the request JSON can only be 'click'
* Only three By selectors are supported viz *TEXT*, *DESC*, *CLAZZ*. This should be assigned to *'byCategory'* field of *'params'*
* The actual text / description / class name should be assigned to *'byValue'* field of *'params'*
* Client should make requests to *http://host:port/execute-command* passing the request JSON in the body and *content-type* header must contain "application/json" as we support only JSON requests

### Port forwarding
* I'm using *Postman HTTP client* to test the Bootstrap server manually
* Even though the HTTP server will be running till the *shutdown* command is issued or test is stoppped, the server is accessible only inside the android device or the emulator
* Due to some reason, whenever I open browser in my emulator, browser application crashes. Also for testing, its better to test the API from machine
* To access the IP:PORT within device from machine, we need to port forward
```sh
        ./adb forward tcp:8080 tcp:8080
```
* I have hardcoded 8080 as the port in *Bootstrap.java* which can be modified to any valid port number
```
        new HttpServer(8080);
```
