# setup script for HttpsSenderTest
ALIAS=$1
echo "Cleanup - removing old files"
rm my_keystore.keystore moshe_cert.crt
echo "Creating new keystore with a single self signed certificate"
printf "a\nb\nc\nd\ne\nf\nyes" | keytool -genkey -keyalg RSA -keysize 2048 -alias $ALIAS -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 3600 -keystore my_keystore.keystore -storepass password123 -deststoretype pkcs12
echo "Printing the certificate into a file"
keytool --list --keystore my_keystore.keystore -rfc -storepass password123 > moshe_cert.crt
echo "Adding the created certificate to the truststore of the jdk located at "$JAVA_HOME
echo "If your jdk is located elsewhere, add the certificate to the appropriate jdk manually"
sudo keytool -import -alias $ALIAS -keystore $JAVA_HOME/jre/lib/security/cacerts -file moshe_cert.crt -storepass changeit
