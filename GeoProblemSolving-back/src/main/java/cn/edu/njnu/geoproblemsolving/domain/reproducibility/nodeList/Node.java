package cn.edu.njnu.geoproblemsolving.domain.reproducibility.nodeList;

import lombok.Data;

@Data
public class Node {
    String name;
    String description;
    String type;  //dataProcessing/simulationDeduction/visualization/resultAnalysis
    String step;  //nodeStep
    String ref;   //ref toolId or modelId
}
