gem 'minitest'
require 'minitest/autorun'
require_relative 'util'
require 'pp'

class TestGraph < Minitest::Test
  def test_graph
    metros, routes = parse(['../data/map_test1.json', '../data/map_test2.json'])
    graph = buildGraph(metros, routes)
    assert_equal(["MEX", 23400000], graph.biggestCity())
    assert_equal(["MIA", 5400000], graph.smallestCity())
    assert_equal(11081250, graph.averageCityPopulation())
    assert_equal(['Lima', 'Washington'].to_set, graph.getHub('name'))
    assert_equal('LIM', graph.vertexInfo('LIM').code)
    assert_equal([["SCL", 2453], ["MEX", 4231], ["BOG", 1879]].to_set, graph.neighboursInfo('LIM', 'code', 'distance'))
    assert_equal(13368, graph.routeDistance(['SCL', 'LIM', 'MEX', 'LIM', 'SCL']))
    assert_equal(3676.2, graph.routeCost(['SCL', 'LIM', 'MEX', 'LIM', 'SCL']))
    assert_equal(25.290666666666663, graph.routeTime(['SCL', 'LIM', 'MEX', 'LIM', 'SCL']))
    assert_equal([6684, ['SCL', 'LIM', 'MEX']], graph.shortestPath('SCL', 'MEX'))
  end

  def test_edit
    metros, routes = parse(['../data/map_test1.json', '../data/map_test2.json'])
    graph = buildGraph(metros, routes)
    graph.removeCity('LIM')
    assert_equal(['Washington'].to_set, graph.getHub('name'))
    graph.removeRoute('MIA', 'WAS')
    assert_equal([["YYZ", 575], ["NYC", 334]].to_set, graph.neighboursInfo('WAS', 'code', 'distance'))
    graph.editCity('MEX', 'population', 1)
    assert_equal(1, graph.vertexInfo('MEX').population)
  end

  def test_io
    metros, routes = parse(['../data/map_test1.json', '../data/map_test2.json'])
    graph = buildGraph(metros, routes)
    graph.addCity('CMI', 'Champaign', 'US', 'North America', -6, {'N'=> 40, 'W'=> 80}, 226000, 1)
    graph.addRoute('CMI', 'NYC', 1208)
    graph.addRoute('CMI', 'MEX', 2518)
    graph.addRoute('CMI', 'YYZ', 831)
    graph.addRoute('CMI', 'MIA', 1757)
    graph.dump('../data/map_test3.json')
    metros, routes = parse(['../data/map_test3.json'])
    graph = buildGraph(metros, routes)
    assert_equal(['CMI', 226000], graph.smallestCity())
    assert_equal(['Champaign'].to_set(), graph.getHub('name'))
  end
end
