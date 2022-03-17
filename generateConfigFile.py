import random as r
import string as s

passwordOpt = s.ascii_uppercase + s.ascii_lowercase + s.digits

array = ["João", "Luís", "José", "Alexandre", "Tiago", "Miguel", "Pedro", "Tomas",
         "Rodrigo", "Fransico", "Guillherme", "Santiago", "Renato", "Alberto", "Carlos"]
arrayf = ["Inês", "Joana", "Tatiana", "Rita", "Ana", "Maria", "Mariana",
          "Cristina", "Teresa", "Francisca", "Carolina", "Mafalda", "Camila"]
fac_dep = ["FCTUC - DEEC", "FCTUC - DEI", "FCTUC - DEQ", "FCTUC - DARQ",
           "FCTUC - DEM", "FCTUC - DF", "FCTUC - DQ", "FCTUC - DM"]
texto = []
for i in range(10):
    password = ""
    CCNumber = ""
    phoneNumber = "9"

    for i in range(8):
        password += r.choice(passwordOpt)
        CCNumber += r.choice(s.digits)
        phoneNumber += r.choice(s.digits)
    nc = ""
    posa = 0
    sexo = r.randint(0, 1)
    if (sexo == 1):
        x = r.randint(1, 2)
        for l in range(x):
            pos = r.randint(0, 12)
            if(pos == posa):
                pos = pos+1
            posa = pos
            nc = nc+array[pos]+" "
        pos1 = r.randint(0, 12)
        pos2 = r.randint(0, 12)
        pos3 = r.randint(0, 5)
        if(pos2 == pos1):
            pos2 = pos2-1

        if nc.lower() not in texto:
            nc = nc.lower().replace(" ", "") + "\t" + password + "\t" + fac_dep[int(i/6)-1] + "\t" + CCNumber + "\t" + phoneNumber + "\t"+str(
                r.randint(0, 30))+"/"+str(r.randint(1, 13))+"/" + str(r.randint(2022, 2027)) + "\n"
            texto.append(nc)

    else:
        x = r.randint(1, 2)
        for l in range(x):
            pos = r.randint(0, 12)
            if (pos == posa):
                pos = pos + 1
            posa = pos
            nc = nc + arrayf[pos] + " "
        pos1 = r.randint(0, 12)
        pos2 = r.randint(0, 12)
        pos3 = r.randint(0, 5)
        if (pos2 == pos1):
            pos2 = pos2 - 1

        if nc.lower() not in texto:
            nc = nc.lower().replace(" ", "") + "\t" + password + "\t" + fac_dep[int(i/6)-1] + "\t" + CCNumber + "\t" + phoneNumber + "\t"+str(
                r.randint(0, 30))+"/"+str(r.randint(1, 13))+"/" + str(r.randint(2022, 2027)) + "\n"
            texto.append(nc)


f = open("C:/Users/Tiago Oliveira/OneDrive - Universidade de Coimbra/3º Ano/SD/ucDrive/src/userdata.txt", "w")
for j in texto:
    f.write(j)
f.close()
