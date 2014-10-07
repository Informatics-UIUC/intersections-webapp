app = angular.module 'fbEvents', ['ui.bootstrap', 'ui.grid', 'ngTable']

app.service 'DataService', ['$http', ($http) ->

  @searchByQuery = (query) ->
    $http.get("searchByQuery?q=#{query}")
      .then (response) ->
        response.data = response.data.map (e) ->
          id:           e._id
          name:         e.name ? null
          description:  e.description ? null
          startTime:    new Date(e.start_time.$date).toLocaleString()
          endTime:      if e.end_time? then new Date(e.end_time.$date).toLocaleString() else null
          location:     e.location ? null
          ownerName:    e.owner.name ? null
        response

  @searchByTime = (timeQuery) ->
    query = []
    if timeQuery.startDate? then query.push "start=#{timeQuery.startDate.toISOString()}"
    if timeQuery.endDate? then query.push "end=#{timeQuery.endDate.toISOString()}"
    query = query.join("&")

    $http.get("searchByTime?#{query}")
      .then (response) ->
        response.data = response.data.map (e) ->
          id:           e._id
          name:         e.name ? null
          description:  e.description ? null
          startTime:    new Date(e.start_time.$date).toLocaleString()
          endTime:      if e.end_time? then new Date(e.end_time.$date).toLocaleString() else null
          location:     e.location ? null
          ownerName:    e.owner.name ? null
        response

  return
]

app.controller 'SearchController', ['$scope', 'DataService', '$log', 'ngTableParams', ($scope, dataService, $log, ngTableParams) ->
  $scope.model =
    query: null
    timeQuery:
      startDate: null
      endDate: null
    events: []

  $scope.datePickerOptions =
    showWeeks: false

  $scope.gridOptions =
    enableSorting: true
    data: $scope.model.events

  $scope.openStartDate = ($event) ->
    $event.preventDefault()
    $event.stopPropagation()
    $scope.model.startDateOpened = true

  $scope.openEndDate = ($event) ->
    $event.preventDefault()
    $event.stopPropagation()
    $scope.model.endDateOpened = true

  $scope.searchByQuery = () ->
    dataService.searchByQuery($scope.model.query).then(
      (response) ->
          $scope.model.status = null
          $scope.model.events = response.data
          $scope.gridOptions.data = response.data
      (error) ->
        $log.error "Got error: #{error.statusText}"
        $scope.model.status = "Error searching by query! Reason: #{error.statusText}"
    )

  $scope.searchByTime = () ->
    dataService.searchByTime($scope.model.timeQuery).then(
      (response) ->
        $scope.model.status = null
        $scope.model.events = response.data
        $scope.gridOptions.data = response.data
      (error) ->
        $log.error "Got error: #{error.statusText}"
        $scope.model.status = "Error searching by time query! Reason: #{error.statusText}"
    )

  $scope.checkValidTimeInputs = (elm1, elm2) ->
    start = $scope.model.timeQuery.startDate
    end = $scope.model.timeQuery.endDate

    if not start? or not end?
      elm1.$setValidity elm1.$name, true
      elm2.$setValidity elm2.$name, true
    else
      elm1.$setValidity elm1.$name, end > start
      if elm2.$invalid then elm2.$setValidity elm2.$name, end > start

  $scope.$watch "model.events", () ->
    $scope.tableParams.page 1
    $scope.tableParams.total = () -> $scope.model.events.length
    $scope.tableParams.reload()

  $scope.tableParams = new ngTableParams(
    page: 1            # show first page
    count: 10          # count per page
  ,
    total: 0
    getData: ($defer, params) ->
      $defer.resolve $scope.model.events.slice((params.page() - 1) * params.count(), params.page() * params.count())
  )

  return
]
