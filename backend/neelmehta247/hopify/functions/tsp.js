const _ = require('lodash')
const Kruskal = require('kruskal');

/**
 * An API that can provide an optimal solution the Travelling Salesman Problem
 * @param {array} vertices the list of all vertices. First has to be start point.
 * @param {array} edges the pairs of indices from the edges
 * @param {object} edgeMap an object where every key is a vertex that has distances to other vertexes
 * @returns {array}
 */
module.exports = async (vertices = [], edges = [], edgeMap = {}, context) => {
    // vertices = ['a', 'b', 'c', 'd', 'e'];
    // edges = [
    //     [0, 1], [1, 0],
    //     [0, 2], [2, 0],
    //     [0, 3], [3, 0],
    //     [0, 4], [4, 0],
    //     [2, 1], [1, 2],
    //     [3, 1], [1, 3],
    //     [4, 1], [1, 4],
    //     [2, 3], [3, 2],
    //     [2, 4], [4, 2],
    //     [3, 4], [4, 3]
    // ];
    // edgeMap = {
    //     'a': {
    //         'b': 30,
    //         'c': 22,
    //         'd': 25,
    //         'e': 10
    //     },
    //     'b': {
    //         'a': 30,
    //         'c': 35,
    //         'd': 40,
    //         'e': 25
    //     },
    //     'c': {
    //         'b': 35,
    //         'a': 22,
    //         'd': 10,
    //         'e': 35
    //     },
    //     'd': {
    //         'b': 40,
    //         'c': 10,
    //         'a': 25,
    //         'e': 20
    //     },
    //     'e': {
    //         'b': 25,
    //         'c': 35,
    //         'd': 20,
    //         'a': 10
    //     }
    // };

    const dist = (a, b) => {
        return edgeMap[a][b];
    }

    const edgeMST = Kruskal.kruskal(vertices, edges, dist);

    // From the list of edge pairs, we now have to create an internal structure.
    const generateTree = (list, start) => {
        if (list.length === 0) {
            let toReturn = {};
            toReturn.array = [];
            toReturn[start] = {};

            return toReturn;
        }

        const tempData = {};
        let array = [];

        list.forEach(element => {
            if (element[0].toString() === start.toString()) {
                tempData[element[1]] = {};
            } else if (element[1].toString() === start.toString()) {
                tempData[element[0]] = {};
            } else {
                array.push(element);
            }
        });

        let info = {};
        for (const key in tempData) {
            const newInfo = generateTree(array, key);
            array = newInfo.array;
            info[key] = newInfo[key];
        }

        const toReturn = { array };
        toReturn[start] = info;

        return toReturn;
    }

    // Once we have the tree, get the top level key, `edgeMST[0][0]` and use that
    // to serialize data
    const firstElem = edgeMST[0][0].toString();
    const tree = generateTree(edgeMST, firstElem)[firstElem];

    const generateOutput = (start, treeList) => {
        let array = [start];
        for (const key in treeList) {
            const returnedArray = generateOutput(key, treeList[key]);
            array = array.concat(returnedArray);
            array.push(start);
        }

        return array;
    }

    const output = generateOutput(firstElem, tree);

    // Now we essentially deduplicate the data to get rid of the random paths in the middle
    const deduped = _.uniq(output);
    return deduped;
}
