app = angular.module 'fbEvents', ['ui.bootstrap']

app.service 'dataService', ['$http', ($http) ->
  @searchEventsByKeyword = (query) ->
    $http.get("search?q=#{query}").then (data) ->
      data.map (e) ->
        e.id = e._id
        e.startTime = new Date(e.start_time.$date).toLocaleString()
        if e.end_time? then e.endTime = new Date(e.end_time.$date).toLocaleString()
        e
]

app.controller 'DBController', ['$scope', '$http', ($scope, $http) ->
  $scope.events = []

  $scope.findEventsByKeyword = (keyword) ->
    $http.get("find?query=#{keyword}").success (data) ->
      # console.log data
      $scope.events = data.map (e) ->
        id: e._id
        name: e.name
        description: e.description if e.description?
        startTime: new Date(e.start_time.$date).toLocaleString()
        endTime: new Date(e.end_time.$date).toLocaleString() if e.end_time?
      # console.log $scope.events
      # console.log "-------------"

  $scope.searchEventsByKeyword = (keyword) ->
    $http.get("search?query=#{keyword}").success (data) ->
      # console.log data
      $scope.events = data.map (e) ->
        e.id = e._id
        e.startTime = new Date(e.start_time.$date).toLocaleString()
        if e.end_time? then e.endTime = new Date(e.end_time.$date).toLocaleString()
        e
]