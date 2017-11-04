require 'pp'
require 'json'
require 'set'
require_relative 'metro'
require_relative 'route'
require_relative 'cs_air'

# parse json file and return a set of metros and routes
def parse(fnames)
  metro_set = Set.new
  route_set = Set.new

  fnames.each do |fname|
    file = File.read(fname)
    data = JSON.parse(file)

    metros = data['metros']
    routes = data['routes']

    metros.each do |metro|
      metro_set.add(Metro.new(metro['code'], metro['name'], metro['country'], metro['continent'],
                              metro['timezone'], metro['coordinates'], metro['population'], metro['region']))
    end

    routes.each do |route|
      route_set.add(Route.new(route['ports'], route['distance']))
    end
  end
  return metro_set, route_set
end

# return CSAir object given metros and routes
def buildGraph(metros, routes)
  metro_hash = Hash.new
  route_hash = Hash.new

  metros.each do |metro|
    metro_hash[metro.instance_variable_get('@code')] = metro
  end
  routes.each do |route|
    route_hash[route.ports] = route
    route_hash[[route.ports[1], route.ports[0]]] = Route.new([route.ports[1], route.ports[0]], route.distance)
  end

  return CSAir.new(metro_hash, route_hash)
end