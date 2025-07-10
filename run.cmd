docker run -it ^
--name docbox ^
-p 8080:80 ^
-e APACHE_HTTPD_PORT="8080" ^
-e DOCBOX_HOSTURL=http://localhost ^
-e DOCBOX_REPOSITORY_ACTIONS_DROP="YES" ^
-v C:\Temp\docbox:/var/www/html/ ^
-v C:\Temp\logs:/var/log/ ^
-d raumzeitfalle/docbox:0.7.0
