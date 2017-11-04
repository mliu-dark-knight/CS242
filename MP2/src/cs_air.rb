require_relative 'graph'
require 'pp'
require 'json'

class CSAir < Graph
  # get name of all cities
  def allCity()
    return allVerticesInfo('name')
  end

  # get longest single flight
  def longestSingleFlight()
    return reduceVariable('edges', 'distance', '>')
  end

  # get shortest single flight
  def shortestSingleFlight()
    return reduceVariable('edges', 'distance', '<')
  end

  # get average flight distance
  def averageFlightDistance()
    return averageVariable('edges', 'distance')
  end

  # get city with largest population
  def biggestCity()
    return reduceVariable('vertices', 'population', '>')
  end

  # get city with smallest population
  def smallestCity()
    return reduceVariable('vertices', 'population', '<')
  end

  # get average population of all cities
  def averageCityPopulation()
    return averageVariable('vertices', 'population')
  end

  # return a dictionary whose key is continent and value is all cities in it
  def aggregateByContinent()
    continents = Hash.new
    @vertices.each do |key, value|
      if not continents.has_key? value.continent
        continents[value.continent] = Set.new
      end
      continents[value.continent].add(value.name)
    end
    return continents
  end

  # open map in browser
  def visualize()
    url = 'http://www.gcmap.com/mapui?P='
    allEdges().each do |pair|
      url += (pair[0] + '-' + pair[1] + ',+')
    end
    url = url[0..-3]
    system('open', url)
  end

  # remove city with code
  def removeCity(code)
    if not @vertices.has_key? code
      puts('City does not exist')
      return
    end
    @vertices.delete(code)
    @neighbours.delete(code)
    @neighbours.each do |key, value|
      value.delete(code)
      @edges.delete([key, code])
      @edges.delete([code, key])
    end
  end

  # remove route with srcCode and destCode
  def removeRoute(srcCode, destCode)
    if not @edges.has_key? [srcCode, destCode]
      puts('Route does not exist')
      return
    end
    @edges.delete([srcCode, destCode])
    @edges.delete([destCode, srcCode])
    @neighbours[srcCode].delete(destCode)
    if @neighbours[srcCode].empty?()
      @neighbours.delete(srcCode)
      @vertices.delete(srcCode)
    end
    @neighbours[destCode].delete(srcCode)
    if @neighbours[destCode].empty?()
      @neighbours.delete(destCode)
      @vertices.delete(destCode)
    end
  end

  # add city with associated information
  def addCity(code, name, country, continent, timezone, coordinates, population, region)
    if @vertices.has_key? code
      puts('City already exists')
      return
    end
    if population <= 0
      puts('Population cannot be negative')
      return
    end
    @vertices[code] = Metro.new(code, name, country, continent, timezone, coordinates, population, region)
    @neighbours[code] = Set.new
  end

  # add route with associated information
  def addRoute(srcCode, destCode, distance)
    if @edges.has_key? [srcCode, destCode] or @edges.has_key? [destCode, srcCode]
      puts('Route already exists')
      return
    end
    if not @vertices.has_key? srcCode or not @vertices.has_key? destCode
      puts('Add route failed because city does not exist')
      return
    end
    if distance <= 0
      puts('Distance cannot be negative')
      return
    end
    @edges[[srcCode, destCode]] = Route.new([srcCode, destCode], distance)
    @edges[[destCode, srcCode]] = Route.new([destCode, srcCode], distance)
    @neighbours[srcCode].add(destCode)
    @neighbours[destCode].add(srcCode)
  end

  # edit city, set attribute of city with code to value
  def editCity(code, attr, value)
    if not @vertices.has_key? code
      puts('City does not exist')
      return
    end
    if not @vertices[code].instance_variable_defined?('@' + attr)
      puts('Attribute does not exist')
      return
    end
    if attr == 'population' and value <= 0
      puts('Population cannot be negative')
      return
    end
    @vertices[code].instance_variable_set('@' + attr, value)
  end

  # edit route, set attribute with srcCode and destCode to value
  def editRoute(srcCode, destCode, attr, value)
    if not @edges.has_key? [srcCode, destCode]
      puts('Route does not exist')
      return
    end
    if not @edges[[srcCode, destCode]].instance_variable_defined?('@' + attr)
      puts('Attribute does not exist')
      return
    end
    if attr == 'distance' and value <= 0
      puts('Distance cannot be negative')
      return
    end
    @edges[[srcCode, destCode]].instance_variable_set('@' + attr, value)
    @edges[[srcCode, destCode]].instance_variable_set('@' + attr, value)
  end

  # dump to json format
  def dump(fname)
    metros = []
    routes = []
    @vertices.each do |key, value|
      metros.push(value.tojson())
    end
    @edges.each do |key, value|
      routes.push(value.tojson())
    end
    dict = Hash.new
    dict['metros'] = metros
    dict['routes'] = routes
    File.open(fname,"w") do |f|
      f.write(JSON.pretty_generate(dict))
    end
  end

  # distance of route, metros is an array of city code
  def routeDistance(metros)
    distance = 0
    metros.each_with_index do |val, idx|
      if idx + 1 == metros.length
        break
      end
      if not @edges.has_key? [val, metros[idx + 1]]
        puts('Route does not exist')
        return -1
      end
      distance += @edges[[val, metros[idx + 1]]].distance
    end
    return distance
  end

  # cost of route, metros is an array of city code
  def routeCost(metros)
    cost = 0
    per_kilo = 0.35
    metros.each_with_index do |val, idx|
      if idx + 1 == metros.length
        break
      end
      if not @edges.has_key? [val, metros[idx + 1]]
        puts('Route does not exist')
        return -1
      end
      cost += per_kilo * @edges[[val, metros[idx + 1]]].distance
      per_kilo -= 0.05
      if per_kilo <= 0
        break
      end
    end
    return cost
  end

  # time from city with srcCode to city with destCode
  def time(srcCode, destCode)
    if @edges[[srcCode, destCode]].distance < 400
      return 2 * Math.sqrt(@edges[[srcCode, destCode]].distance * 400.0 / (750**2))
    end
    return 400.0 / 375.0 + (@edges[[srcCode, destCode]].distance - 400) / 750.0
  end

  # time of route, metros is an array of city code
  def routeTime(metros)
    time = 0
    metros.each_with_index do |val, idx|
      if idx + 1 == metros.length
        break
      end
      if not @edges.has_key? [val, metros[idx + 1]]
        puts('Route does not exist')
        return -1
      end
      time += time(val, metros[idx + 1])
      if idx != 0
        time += (2 - (@neighbours[val].length() - 1) / 6.0)
      end
    end
    return time
  end

  # shortest path from city with srcCode to city with destCode
  def shortestPath(srcCode, destCode)
    if not @vertices.has_key? srcCode or not @vertices.has_key? destCode
      puts('City does not exist')
      return nil
    end
    return dijkstra(srcCode, destCode, 'distance')
  end
end