class Metro
  attr_accessor :code, :name, :country, :continent, :timezone, :coordinates, :population, :region
  def initialize(code, name, country, continent, timezone, coordinates, population, region)
    @code = code
    @name = name
    @country = country
    @continent = continent
    @timezone = timezone
    @coordinates = coordinates
    @population = population
    @region = region
  end

  def tojson()
    dict = Hash.new
    dict['code'] = @code
    dict['name'] = @name
    dict['country'] = @country
    dict['continent'] = @continent
    dict['timezone'] = @timezone
    dict['coordinates'] = @coordinates
    dict['population'] = @population
    dict['timezone'] = @timezone
    return dict
  end

end