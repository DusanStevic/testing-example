Uputstvo za povezivanje Spring Boot projekta sa GitHuba sa SonarCloud, TravisCI i Heroku servisima.
Ovo je okvirno uputstvo koje možda neće svima odraditi posao.
Ideja je da okvirno prikaže koji se servisi mogu uvezati i koristiti kako bi se povećao kvalitet izrade i održavanja projekta.
Možda će nekome pomoći :blush:

- Ideja
![Flow](/assets/flow.png)

# Heroku Deployment

- Kreirati nalog na [Heroku](https://heroku.com)
- Kreirati novu aplikaciju
- Ukoliko želite da koristite Postgres bazu potrebno je otići na Resources tab aplikacije, ići na *Find more add-ons* i odabrati Heroku Postgres
![Find more add-ons](/assets/find_more_addons.png)
![Heroku Postgres](/assets/postgres.png)
- Aktivacijom Postgres baze generisaće se konekcioni parametri koji se mogu sakriti iz konfiguracionih fajlova projekta tako što će se otići na *Settings* tab -> *Reveal Config Vars* -> i upisati kao *key:value* parove
![Postgres Credentials](/assets/postgres_creds.png)
![Heroku Config Vars](/assets/config_vars.png)
- Ažurirati `application.properties` da kredencijali odgovaraju konfiguracionim promenljivama (videti `application.properties` projekta)
- Ako je projekat upakovan kao `war` fajl, potrebno je dodati fajl sa nazivom `Procfile` (bez ekstenzije) u root folder projekta sa sledećim sadržajem:
```
  web: java $JAVA_OPTS -jar target/testing-example-0.0.1-SNAPSHOT.war --server.port=$PORT $JAR_OPTS
```
- U `application.properties` fajl dodati:
```
server.port=${PORT:8080}
```
- Ako GitHub ne prepoznaje projekat kao Java project dodati `system.properties` sa sledećim sadržajem:
```
java.runtime.version=1.8
```
- Na *Deploy* tabu za *Deployment method* odabrati GitHub (najlakša varijanta)
- U odeljku *App connected to GitHub* odabrati projekat sa GitHuba za koji radite deploy
- Možete uključiti automatski redeloy koji će se aktivirati pri svakom novom commitu
- Manualni deploy možete odraditi u sekciji *Manual deploy* gde možete da odaberete granu koju želite da deployujete
![Heroku - Github integration](/assets/heroku_github.png)
- Ukoliko nije aktivan dyno instalirati [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli), ulogovati se kroz terminal i ukucati komandu:

```
$ heroku ps:scale web=1 --app <naziv_aplikacije>
```

# TravisCI

- Napraviti nalog na [Travis CI](https://travis-ci.org) (povezati se sa GitHubom)
- Odabrati repozitorijum koji treba da uđe u CI ciklus
![Travis CI Repo](/assets/travisci_repo.png)
- Na *Settings* tabu projekta dodati *Environment Variablu* kao key:value par Heroku API ključ koji se može naći na Heroku profilu korisnika, na tabu *Account* pod sekcijom *API Key*
![Heroku API Key](/assets/heroku_api_key.png)
![Travis CI Environment Variables](/assets/travisci_env_var.png)
- Napraviti `.travis.yml` fajl u root folderu projekta
```
sudo: required
language: java
jdk: oraclejdk8

before_install:
  - chmod +x mvnw

script:
  - ./mvnw clean install -DskipTests=false -B

deploy:
  provider: heroku
  api_key: $HEROKU_API_KEY
  app: <naziv_aplikacije>
```

- Otići na Heroku nalog, na tab *Deploy* aplikacije i u odeljku *Automatic deploys* štiklirati *Wait for CI to pass before deploy* kako bi TravisCI odradio deployment na Heroku tek kada build prođe
![Travis CI and Heroku Chain](/assets/heroku_wait_ci.png)

# SonarCloud

- Napraviti nalog na [SonarCloud](https://sonarcloud.io) (povezati se sa GitHubom)
- U gornjem desnom uglu nalazi se dugme *+* gde je potrebno odabrati *Analyze new project* i povezati svoj repozitorijum sa SonarCloudom
![Sonar Add New Project](/assets/sonar_add.png)
![Sonar Repo](/assets/sonar_repo.png)
- Ići na *Setup*, generisati i sačuvati **Token**
- Odabrati glavni programski jezik i build alat i dobićete generisanu komandu za pokretanje sonara u mavenu
![Sonar App](/assets/sonar_analyze.png)
- U Travis konfiguraciju dodati *Environment Variable* SONAR_TOKEN
![Travis CI Sonar Env Var](/assets/travisci_sonar_var.png)
- Ažurirati `.travis.yml` fajl
```
sudo: required
language: java
jdk: oraclejdk8

before_install:
  - chmod +x mvnw

addons:
  sonarcloud:
  organization: "stojkovm-github"
  token: $SONAR_TOKEN

script:
  - ./mvnw clean install -DskipTests=false -B
  - ./mvnw sonar:sonar -Dsonar.projectKey=<generisani_project_key> -Dsonar.organization=stojkovm-github -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$SONAR_TOKEN

deploy:
  provider: heroku
  api_key: $HEROKU_API_KEY
  app: <naziv_aplikacije>
```
- Ako želite da dodate podršku za Code Coverage potrebno je u `pom.xml` fajl dodati plugin
```
  <plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.2</version>
    <executions>
      <execution>
        <id>default-prepare-agent</id>
        <goals>
          <goal>prepare-agent</goal>
        </goals>
      </execution>
      <execution>
        <id>default-report</id>
        <phase>prepare-package</phase>
        <goals>
          <goal>report</goal>
        </goals>
      </execution>
    </executions>
  </plugin>
```
Kada je sve povezano ili će sve raditi:
<img src="/assets/bravo.gif" width="250" height="200">
Ili neće:
<img src="/assets/lol.gif" width="250" height="200">
