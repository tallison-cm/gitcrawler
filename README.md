<h1>GitCrawler</h1>
GitCrawler is a RESTful Webservice built on Spring Boot.


<h2>Installation</h2>


<h3>Git</h3>
1.  Clone the project to your machine <br>
2.  Import as a Maven project to your IDE <br>
3.  Run as a Java Application <br>

<h3>Docker</h3>
1. Download the Docker image: docker pull tallisoncm/git-crawler:first <br>
2. Run: docker run -p 8080:8080 tallisoncm/git-crawler:first <br>

<h2>How to use</h2>
To use it, you just call the endpoint with a GitHub repository URL:

  http://localhost:8080/gitcrawler?url=<your_url>
  
  
For example:

  http://localhost:8080/gitcrawler?url=https://github.com/tallison-cm/todo_list
  
Will return:
  
  [{"name":"py","lines":198,"size":5577.0},{"name":"sqlite3","lines":0,"size":143360.0}]
 
<h3>AWS</h3>
There is a service running on AWS, so you just need to run it in a browser

  http://3.19.246.187:8080/gitcrawler?url=<your_url>
