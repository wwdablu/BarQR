# BarQR
Application to scan and fetch barcode and QR code data using zxing library.

The application uses Zxing library to scan both bar codes and QR codes. The application can then parse the information and display it to the user. Based on the type of the data, the required action can be performed.

* Note, VCard support is not present.

# Type and Data
* Web URL
    Launches the browser and opens the URL
    
* E-Mail
    Launch the mail client with the to, body and subject populated from the QR data
    
* SMS
    Launch the SMS application with the to and body populated from the QR data
    
* PHONE
    Launch the dialer application with the number to dial.
