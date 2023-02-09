Company(name,country,zip,streetInfo,phoneNumber)
PRIMARY KEY (Company) = <name>
FOREIGN KEY Company (zip) REFERENCES ZipControl (zip)

E_mail(name,e_mail)
PRIMARY KEY (E_mail) = <name,e_mail>
FOREIGN KEY E_mail (name) REFERENCES Company (name)

Product(id,name,description,brandName)
PRIMARY KEY (Product) = <id>

Produce(id,name,product_id,capacity)
PRIMARY KEY (Produce) = <id>
FOREIGN KEY Produce (name) REFERENCES Company (name)
FOREIGN KEY Produce (product_id) REFERENCES Product (id)

Transaction(id,name,product_id,amount,createdDate)
PRIMARY KEY (Transaction) = <id>
FOREIGN KEY Transaction (name) REFERENCES Company (name)
FOREIGN KEY Transaction (product_id) REFERENCES Product (id)

TransactionHistory(transactionHistId,company,product,amount,createdDate)
PRIMARY KEY (TransactionHistory) = <transactionHistId>