<h1>Microservices Signature Project</h1>
<p>Проект, состоящий из двух микросервисов, которые взаимодействуют через Redis для генерации и подписи данных с использованием ECDSA.</p>

<h2>Архитектура</h2>
<p>Проект состоит из трех основных компонентов:</p>
<ul>
  <li>Service One (порт 8082) - генерирует случайные данные и сохраняет в Redis</li>
  <li>Service Two (порт 8083) - подписывает данные из Redis с использованием ECDSA</li>
  <li>Redis (порт 6379) - кэш для обмена данными между сервисами</li>
</ul>

<h2>Технологии</h2>
<ul>
  <li>Java 17</li>
  <li>Spring Boot 2.7.0</li>
  <li>Redis</li>
  <li>ECDSA (Elliptic Curve Digital Signature Algorithm)</li>
  <li>Docker & Docker Compose</li>
  <li>Maven</li>
</ul>

<h2>Функциональность</h2>

<p>Service One (/process)</p>
<ul>
  <li>Генерирует 200KB случайных данных</li>
  <li>Сохраняет данные в Redis</li>
  <li>Использует публичный ключ для верификации подписей</li>
</ul>

<p>Service Two (/sign)</p>
<ul>
  <li>Получает данные из Redis</li>
  <li>Подписывает данные с использованием приватного ключа ECDSA</li>
  <li>Сохраняет подпись в Redis</li>
<ul>
