Company (name :string, country :string, zip :string, city :string, streetInfo :string, phoneNumber :string)
PRIMARY KEY(Company) = <name>
CANDIDATE KEY (Company) = <phoneNumber>

Emails(name :string, email :string)
PRIMARY KEY(Emails) =<name,email>
FOREIGN KEY Emails(name) REFERENCES Company(name)

Product(id :int,name :string, brandName :string, description :string)
PRIMARY KEY (Product)= <id>

Produce (id :int, name :string, capacity :string)
PRIMARY KEY (Produce) =<id,name>
FOREIGN KEY Produce(id) REFERENCES Product(id)
FOREIGN KEY Produce(name) REFERENCES Company(name)

Transaction(id :int,name :string, amount :string, createdDate :string)
PRIMARY KEY(Transaction)=<id,name>
FOREIGN KEY Transaction(id) REFERENCES Product(id)
FOREIGN KEY Transaction(name) REFERENCES Company(name)