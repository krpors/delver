#!/usr/bin/env python3

"""
This utility parses a Delver generated CSV and writes it back into a tree
which looks like

Java Class Name
================================================
Total: total calls for all methods in this class
================================================
Amount    =>  Method Name 1
Amount    =>  Method Name 2
Amount    =>  Method Name 3
Amount    =>  Method Name n

It can help you to identify usages better.
"""

if __name__ == '__main__':
    classdict = dict()

    # TODO: parameterize the filename using script args
    with open('your-delver-csv-file-here.csv') as f:
        f.readline()
        s = f.readlines()
        for line in s:
            split = line.split(';')

            count = split[0]
            classname = split[6].strip()
            method = split[7].strip()

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

    for key, value in classdict.items():
        total = 0
        stats = ''
        for mkey, mvalue in value.items():
            s = '%-10d => %s' % (mvalue, mkey)
            total += mvalue
            stats += s + '\n'

        print(key)
        print("=" * (len(key)))
        print("Total:", total)
        print("=" * (len(key)))
        print(stats)
        print()

