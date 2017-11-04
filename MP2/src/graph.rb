require 'set'
require 'pp'

class Graph
  attr_accessor :vertices, :edges, :neighbours
  def initialize(vertices, edges)
    unless vertices.is_a? Hash and edges.is_a? Hash
      raise ArgumentError.new('Both nodes and edges have to be Hash')
    end

    @vertices = vertices
    @edges = edges

    initNeighbours()
  end

  # initialize neighbour dictionary, assume graph is directed
  def initNeighbours()
    @neighbours = Hash.new
    @vertices.each do |key, value|
      @neighbours[key] = Set.new
    end

    @edges.each do |key, value|
      if not @vertices.has_key? key[0]
        @vertices[key[0]] = Set.new
      end
      if not @vertices.has_key? key[1]
        @vertices[key[1]] = Set.new
      end

      if not @neighbours.has_key? key[0]
        @neighbours[key[0]] = Set.new
      end
      if not @neighbours.has_key? key[1]
        @neighbours[key[1]] = Set.new
      end

      @neighbours[key[0]].add(key[1])
    end
  end

  # get attribute of all vertices
  def allVerticesInfo(attr)
    info = Set.new
    @vertices.each do |key, value|
      info.add(value.instance_variable_get('@' + attr))
    end
    return info
  end

  # get attribute1 of all neighbours and attribute2 of edges between query and neighbour
  def neighboursInfo(query, attr1, attr2)
    if not @neighbours.has_key? query
      raise ArgumentError.new('Vertex does not exist in graph')
    end

    container = Set.new
    @neighbours[query].each do |neighbour|
      container.add([vertexInfo(neighbour).instance_variable_get('@' + attr1), edgeInfo([query, neighbour]).instance_variable_get('@' + attr2)])
    end
    return container
  end

  # get all info of query
  def vertexInfo(query)
    if not @vertices.has_key? query
      raise ArgumentError.new('Vertex does not exist in graph')
    end
    return @vertices[query]
  end

  # get hub attribute
  def getHub(attr)
    max = -1
    @neighbours.each do |key, value|
      if value.length() > max
        max = value.length
      end
    end

    hubs = Set.new
    @neighbours.each do |key, value|
      if value.length() == max
        hubs.add(vertexInfo(key).instance_variable_get('@' + attr))
      end
    end

    return hubs
  end

  def allEdges()
    @edges.keys
  end

  # get info of query edge
  def edgeInfo(query)
    if not @edges.has_key? query
      raise ArgumentError.new('Edge does not exit in graph')
    end
    return @edges[query]
  end

  # reduce vertices / edges according to attribute
  def reduceVariable(variable, attr, operator)
    optimal_key = instance_variable_get('@' + variable).keys[0]
    optimal_value = instance_variable_get('@' + variable)[optimal_key].instance_variable_get('@' + attr)
    instance_variable_get('@' + variable).each do |key, value|
      if value.instance_variable_get('@' + attr).send(operator, optimal_value)
        optimal_key = key
        optimal_value = value.instance_variable_get('@' + attr)
      end
    end
    return optimal_key, optimal_value
  end

  # return average attribute of vertices / edges
  def averageVariable(variable, attr)
    sum = 0
    instance_variable_get('@' + variable).each do |key, value|
      sum += value.instance_variable_get('@' + attr)
    end
    return sum / instance_variable_get('@' + variable).length
  end

  def dijkstra(srcCode, destCode, attr)
    q = Hash.new
    dist = Hash.new
    prev = Hash.new
    @vertices.each do |key, value|
      dist[key] = Float::INFINITY
      prev[key] = nil
      q[key] = Float::INFINITY
    end
    dist[srcCode] = 0
    q[srcCode] = 0

    while not q.empty?
      u, value = q.min_by{|k,v| v}
      q.delete(u)
      @neighbours[u].each do |v|
        alt = dist[u] + @edges[[u, v]].instance_variable_get('@' + attr)
        if alt < dist[v]
          dist[v] = alt
          prev[v] = u
          q[v] = alt
        end
      end
    end

    distance = 0
    path = []
    v = destCode
    while prev[v] != nil
      path.insert(0, v)
      distance += @edges[[prev[v], v]].instance_variable_get('@' + attr)
      v = prev[v]
    end
    path.insert(0, srcCode)
    return distance, path
  end

end