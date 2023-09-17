![visitor badge](https://vbr.wocr.tk/badge?page_id=isaaclo97.Budget-Influence-Maximization-Problem&color=be54c6&style=flat&logo=Github)
![Manintained](https://img.shields.io/badge/Maintained%3F-yes-green.svg)
![GitHub last commit (master)](https://img.shields.io/github/last-commit/isaaclo97/Budget-Influence-Maximization-Problem)
![Starts](https://img.shields.io/github/stars/isaaclo97/Budget-Influence-Maximization-Problem.svg)

# An efficient and effective GRASP algorithm for the Budget Influence Maximization Problem

Social networks are in continuous evolution, and its spreading has attracted the interest of both practitioners and the scientific community. In the last decades, several new interesting problems have aroused in the context of social networks, mainly due to an overabundance of information, usually named as infodemic. This problem emerges in several areas, such as viral marketing, disease prediction and prevention, and misinformation, among others. Then, it is interesting to identify the most influential users in a network to analyze the information transmitted, resulting in Social Influence Maximization (SIM) problems. In this research, the Budget Influence Maximization Problem (BIMP) is tackled. BIMP proposes a realistic scenario where the cost of selecting each node is different. This is modeled by having a budget that can be spent to select the users of a network, where each user has an associated cost. Since BIMP is a hard optimization problem, a metaheuristic algorithm based on Greedy Randomized Adaptive Search (GRASP) framework is proposed. 

* Paper link: <https://doi.org/10.1007/s12652-023-04680-z>
* Impact Factor: 3.662
* Quartil: Q2 - 57/190 - Computer Science, Artificial Intelligence | Q2 - 75/246 - Computer Science, Information Systems | 2021  <br>
* Journal: Journal of Ambient Intelligence and Humanized Computing

![Influencers](./img/influencers.jpg)

## Datasets

* [soc-Epinions1](https://snap.stanford.edu/data/soc-Epinions1.html)
* [CA-CondMa](https://snap.stanford.edu/data/ca-CondMat.html)
* [CA-HepT](https://snap.stanford.edu/data/ca-HepTh.html)
* [HC Twitter](./twitter/)


All txt format instances can be found also in instances folder.

## Source code

The code´s folder contains the proyect with our algorithmic proposal. We use IntellIj IDE, the proyect can be build with maven, we also add the required java libraries.


## Results

The following excel document contains all the final results divided by instance. It also contains the final tables and the network information table.

* Tables.xlsx


## Executable

You can just run the BIMP.jar as follows.

All the parameters can be customized in the following file: ./source/src/main/resources/application.yml

```
java -jar BIMP.jar --instances.path.default="./instances"
```

Also you can modify the parameters in the launch command, for instance:

```
java -jar BIMP.jar ---instances.path.default="./instances" --algorithm.proyectName="GRASP_1" --algorithm.FO=2 
```

If you want new instances just replace folder instances.
Solution folder contains an excel with the results.

## Cite

Please cite our paper if you use it in your own work:

Bibtext
```
```

MDPI and ACS Style
```
```

AMA Style
```
```

Chicago/Turabian Style
```
```
