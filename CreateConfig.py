import random as r
f1 = open("Teses")
array = f1.readlines()
arrayteses = []
for i in array:
    i = i.replace("\n", "")
    i = i.replace(";", "")
    arrayteses.append(i)
array = ["João", "Luís", "José", "Alexandre", "Tiago", "Miguel", "Pedro", "Tomas",
         "Rodrigo", "Fransico", "Guillherme", "Santiago", "Renato", "Alberto", "Carlos"]
arrayf = ["Inês", "Joana", "Tatiana", "Rita", "Ana", "Maria", "Mariana",
          "Cristina", "Teresa", "Francisca", "Carolina", "Mafalda", "Camila"]
apelidos = ["Aguiar", "Silva", "Sousa", "Pereira", "Pinheiro", "Costa", "Coelho",
            "Ferreira", "Ribeiro", "Batista", "Abreu", "Alves", "Guedes", "Campos"]
gruposnome = ["Adaptive Computation", "Cognitive and Media Systems", "Evolutionary and Complex Systems", "Information Systems",
              "Communications and Telematics", "Software and Systems Engineering"]
acronimos = ["AC", "CMS", "ECOS", "IS", "LCT", "SSE"]
texto = []
for i in range(36):
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

        nc = nc + apelidos[pos1] + " " + apelidos[pos2] + "\t" + nc.lower().replace(" ", "") + apelidos[pos2].lower() + "@dei.uc.pt" + "\t" + gruposnome[int(
            i/6)-1] + "\t" + acronimos[int(i/6)-1] + "\t" + "\t"+arrayteses[i]+"\t"+str(r.randint(0, 30))+"/"+str(r.randint(1, 13))+"/2020\n"
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

        nc = nc + apelidos[pos1] + " " + apelidos[pos2]+"\t" + nc.lower().replace(" ", "")+apelidos[pos2].lower()+"@dei.uc.pt"+"\t"+gruposnome[int(
            i/6)-1]+"\t"+acronimos[int(i/6)-1]+"\t"+"\t"+arrayteses[i]+"\t"+str(r.randint(0, 30))+"/"+str(+r.randint(1, 13))+"/2020\n"
        texto.append(nc)


print(texto)
f = open("/src/userdata.txt", "w")
for j in texto:
    f.write(j)
f.close()
