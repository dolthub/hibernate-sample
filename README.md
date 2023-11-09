

------------------
Setting up your DB

Clone the example database:

```sh
$ dolt clone dolthub/hibernate-sample
$ cd hibernate-sample
```

Create a branch:

```sh
$ dolt branch mybranch HEAD~1
```

Start a dolt sql-server:

`dolt sql-server -u root -p r00tr00t`

(password matters - it's coded into the hibernate config)

-------------------
Building and running the code.

Install Java. I used OpenJDK: https://openjdk.org/install/

Install Maven: https://maven.apache.org/install.html

Then build it:

`$ mvn package`

And run it:

`$ mvn exec:java`


