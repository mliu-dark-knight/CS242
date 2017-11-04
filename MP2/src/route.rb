class Route
  attr_accessor :ports, :distance
  def initialize(ports, distance)
    @ports = ports
    @distance = distance
  end

  def tojson()
    dict = Hash.new
    dict['ports'] = @ports
    dict['distance'] = @distance
    return dict
  end
end