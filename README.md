1. Install Docker Desktop https://www.docker.com/products/docker-desktop/
2. Install  MySQL Community https://dev.mysql.com/downloads/mysql/
   (trường hợp không install MySQL có thể sử dụng phần mền khác nhưng phải install MySQL server 8.0)
3. Install IntelliJ IDEA https://www.jetbrains.com/idea/
   (Eclip nỏ biết support)
4. Bật terminal trong IntelliJ
5. run: docker compose -f docker-compose.yaml up -d (chạy hơi lâu, thế nên đợi chờ là hạnh phúc)
![image](https://github.com/Nguyentruongphivolder9/SuperShop-API/assets/100484492/698d6e0d-3f9b-4315-aabe-9782c1510cee)

7. kiểm tra docker image có chạy hay chưa: docker ps
   ![image](https://github.com/Nguyentruongphivolder9/SuperShop-API/assets/100484492/efa68e50-b8db-4862-91ec-ba76ae2a5f37)
   khi đóng docker container: docker rm -f c4d56d109b17
   (c4d56d109b17: này là container id copy container id của mn xuống thay cho nó)

9. rồi run start
10. vào postman test api
   ![image](https://github.com/Nguyentruongphivolder9/SuperShop-API/assets/100484492/b3a9a7dc-bc45-422f-bb04-e83f72648b32)
11. Vào MySQL Workbench kiểm tra dữ liệu nhé.
    Ai pro thì dùng commandline.
    Nhập giống form còn password là supershop
    ![image](https://github.com/Nguyentruongphivolder9/SuperShop-API/assets/100484492/f7ffc7fd-5944-4ae2-817f-ebe2a3f17162)


   (Lỗi là phải alo Leader gấp)
