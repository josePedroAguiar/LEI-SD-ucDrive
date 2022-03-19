import random as r
import string as s
import time as t

from faker import Faker
from unidecode import unidecode

if __name__ == "__main__":
    fake = Faker("pt_PT")

    passwordOpt = s.ascii_uppercase + s.ascii_lowercase + s.digits

    fac_dep = ["FCTUC - DEEC", "FCTUC - DEI", "FCTUC - DEQ", "FCTUC - DARQ",
               "FCTUC - DEM", "FCTUC - DF", "FCTUC - DQ", "FCTUC - DM"]

    with open("src/userdata.txt", "w") as f:
        f.write("#user settings\n#CCnumber\taddress\tpass\tdepartment\tcell\tuser\texpDate\n\n")

        for _ in range(10):
            password = ""
            CCNumber = ""
            phoneNumber = "9" + r.choice("1236")
            username = fake.user_name()
            address = unidecode(fake.freguesia())
            department = r.choice(fac_dep)
            curYear = int(t.strftime("%Y", t.localtime()))
            expDate = str(r.randint(1, 31)) + "/" + str(r.randint(1, 12)) + "/" + str(r.randint(curYear, curYear + 5))

            for i in range(8):
                password += r.choice(passwordOpt)
                CCNumber += r.choice(s.digits)
                if i < 7:
                    phoneNumber += r.choice(s.digits)

            info = CCNumber + "\t" + address + "\t" + password + "\t" + department + "\t" + phoneNumber + "\t" + username + "\t" + expDate + "\n"
            print(info, end="")
            f.write(info)
