from faker import Faker
import random as r
fake = Faker('pt_PT')
def generatorPassword(l):
    generatedPassword=""
    chars="abcdefghijklmnopqrstuvxywzABCDEFGHIKLMNOPQRSTUVXYWZ"
    chars+="0123456789"
    chars+="!?§#$%&=*+^@"
    for i in range(l):
        generatedPassword+=r.choice(chars)
    return generatedPassword
#CCnumber;address;pass;department;cell;user;expDate;
string="#user settings\n#CCnumbe\taddress\tpass\tdepartment\tcell\tuser\texpDate;\n"
for _ in range(10):
    string+= (fake.ssn().replace("-","")+"\t"+fake.address().replace("\n"," ")+"\t"+generatorPassword(8)+
           "\t"+fake.phone_number().replace(" ","").replace("(351)","").replace("+351","")+"\t"+fake.name()+"\t"+
           str(fake.future_date()).replace("-","/")+"\n")
f=open("usersData.txt","w")
f.write(string)
f.close()