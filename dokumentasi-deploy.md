mvn clean package "-Dmaven.test.skip" (tanpa profile)
mvn clean package "-Dmaven.test.skip" "-Dquarkus.profile=dev" (jika buat profile dev)


Jalankan java -jar ./quarkus-run.jar 


Menjalankan dengan PM2 
misal masuk ke folder quarkus-app terlebih dahulu
pm2 start --name product java -- -jar ./quarkus-app/quarkus-run.jar.

Mengecek logs/list
pm2 logs [id]
pm2 list
