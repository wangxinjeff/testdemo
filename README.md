# Easemob Test
--------
## Using the process
1.Modify your parameters on the MainActivity.kt
2.Modify your parameters on the OtherProcessService.kt
3.Run project tests
4.When the test is complete, click the button to write the CSV file

## information
   
Implementation logic:

    Test login 1000 times: Login ID A, call logout after successful login, call login after 300ms interval, call logout after login failure, count as login times, A total of 1000 login times

    Test sending 1000 messages: start another process to log in to user B. User A sends 1000 messages to user B and sends the next message at an interval of 100ms


Output from the UI:

    Number of successful login and sending

    Minimum login and sending time

    Maximum duration of login and sending

A CSV file is created locally to record related information. The path is as follows:
    /storage/emulated/0/Android/data/com.hyphenate.easetest/files/login_info.csv 	// Login Information
    /storage/emulated/0/Android/data/com.hyphenate.easetest/files/msg_info.csv		// Message Information
