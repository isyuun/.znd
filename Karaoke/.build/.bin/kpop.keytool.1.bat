keytool -exportcert -alias kymedia -keystore "./kr.kymedia.karaoke.kpop.keystore" -storepass is110315 > kpop.cert.bin
openssl sha1 -binary kpop.cert.bin > kpop.sha1.bin
openssl base64 -in kpop.sha1.bin -out kpop.base64.txt 
pause

