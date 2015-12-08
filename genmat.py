# genmat.py -- Matrix generator in Python.
# Copyright (c) Philip Conrad, 2015. All rights reserved.

import sys
import csv


USAGE = """Usage:
    python genmat.py OUTFILE M N

Options:
    OUTFILE : Output file.
    M       : Matrix row dimension.
    N       : Matrix column dimension.
"""

# Generates a list of lists representing an MxN matrix filled with value V.
def gen_matrix(m, n, v):
    out = []
    for i in range(0, m):
        row = [1 for j in range(0, n)]
        out.append(row)
    return out


if __name__ == '__main__':
    try:
        # Read in parameters.
        src = sys.argv[1]
        m   = int(sys.argv[2])
        n   = int(sys.argv[3])
        # Open output file and write rows of matrix.
        with open(src, 'w') as csvfile:
            matrixwriter = csv.writer(csvfile)
            matrix = gen_matrix(m, n, 1)
            matrixwriter.writerows(matrix)
    except IndexError:
        # Otherwise, print usage and bail.
        print USAGE

