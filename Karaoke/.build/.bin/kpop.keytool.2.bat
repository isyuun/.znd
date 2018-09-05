keytool -exportcert -alias kymedia -keystore "./kr.kymedia.karaoke.kpop.keystore" | openssl sha1 -binary | openssl base64
keytool -exportcert -alias kymedia -keystore "./kr.kymedia.karaoke.kpop.keystore" | openssl sha1 -binary | openssl enc -a -e
pause
