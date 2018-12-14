cd /d "%~dp0"
REM cVI4Cm99wGxUyXxdXhO0BI2HgUo=
keytool -exportcert -alias keumyoung -keystore kr.keumyoung.karaoke.jks -storepass rmadudrmfnq1 -keypass rmadudrmfnq1 | openssl sha1 -binary | openssl base64
REM /oi7vzyazAd5sN8KyvQzigQo3zM=
keytool -exportcert -alias androiddebugkey -keystore kr.keumyoung.karaoke.debug.keystore -storepass android -keypass android | openssl sha1 -binary | openssl base64
pause