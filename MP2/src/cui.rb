require_relative 'cs_air'
require_relative 'util'
require 'pp'

def main()
  metros, routes = parse(['../data/map_data.json', '../data/cmi_hub.json'])
  graph = buildGraph(metros, routes)

  while true do
    puts 'Enter your query:'
    query = gets.chomp
    case query
      when '0.1'
        pp graph.allCity()
      when '0.2'
        puts 'Enter city code:'
        code = gets.chomp
        begin
          pp graph.vertexInfo(code)
          pp graph.neighboursInfo(code, 'name', 'distance')
        rescue
          puts 'City code does not exist'
        end
      when '0.3'
        puts 'Enter your sub-query:'
        subquery = gets.chomp
        case subquery
          when 'a'
            pp graph.longestSingleFlight()
          when 'b'
            pp graph.shortestSingleFlight()
          when 'c'
            pp graph.averageFlightDistance()
          when 'd'
            pp graph.biggestCity()
          when 'e'
            pp graph.smallestCity()
          when 'f'
            pp graph.averageCityPopulation()
          when 'g'
            pp graph.aggregateByContinent()
          when 'h'
            pp graph.getHub('name')
        end
      when '0.v'
        graph.visualize()
      when '1.1'
        puts 'Enter your sub-option'
        suboption = gets.chomp
        case suboption
          when '1'
            puts 'Enter city code'
            code = gets.chomp
            graph.removeCity(code)
          when '2'
            puts 'Enter route'
            route = gets.chomp().split(' ')
            graph.removeRoute(route[0], route[1])
          when '3'
            puts 'Enter city info'
            city = gets.chomp().split(' ')
            if city.length != 9
              puts 'Invalid info'
            else
              graph.addCity(city[0], city[1], city[2], city[3], city[4].to_i, {'N'=> city[5].to_i, 'M'=> city[6].to_i}, city[7].to_i, city[8].to_i)
            end
          when '4'
            puts 'Enter route info'
            route = gets.chomp.split(' ')
            if route.length != 3
              puts 'Invalid route'
            else
              graph.addRoute(route[0], route[1], route[2].to_i)
            end
          when '5'
            puts 'Enter city code'
            code = gets.chomp()
            puts 'Enter attribute'
            attr = gets.chomp()
            puts 'Enter value'
            value = gets.chomp()
            graph.editCity(code, attr, value.to_i)
        end
      when '1.2'
        graph.dump('../data/map_full.json')
      when '1.3'
        puts 'Enter your route'
        route = gets.chomp.split(' ')
        puts 'Enter your sub-query'
        puts 'Distance'
        pp graph.routeDistance(route)
        puts 'Cost'
        pp graph.routeCost(route)
        puts 'Time'
        pp graph.routeTime(route)
      when '1.4'
        puts 'Enter route'
        pair = gets.chomp.split(' ')
        pp graph.shortestPath(pair[0], pair[1])
      when 'q'
        break
    end
  end
end

main