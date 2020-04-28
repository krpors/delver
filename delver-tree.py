#!/usr/bin/env python3

if __name__ == '__main__':
    classdict = dict()

    with open('delver-run-03.csv') as f:
        f.readline()
        s = f.readlines()
        for line in s:
            split = line.split(';')

            count = split[0]
            classname = split[6].strip()
            method = split[7].strip()

            if classname.startswith("nl.rws.datalab.ds.clients.nlrdms"):
                continue

            # check if class exists in dict
            if classname in classdict:
                # now check if there is a methoddict
                methoddict = classdict[classname]

                if method in methoddict:
                    methoddict[method] += int(count)
                else:
                    methoddict[method] = int(count)
            else:
                methoddict = dict()
                # add classname with empty dict
                classdict[classname] = methoddict
                # add method with count
                methoddict[method] = int(count)

#    print(classdict)

    for key, value in classdict.items():
        total = 0
        stats = ''
        for mkey, mvalue in value.items():
            s = '%-10d => %s' % (mvalue, mkey)
            total += mvalue
            stats += s + '\n'

        print(key)
        print("=" * (len(key)))
        print("TOTAL:", total)
        print("=" * (len(key)))
        print(stats)
        print()

