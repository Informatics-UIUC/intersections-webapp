app = angular.module 'fbEvents', ['ui.bootstrap', 'ngTable', 'cgBusy']

app.value('cgBusyDefaults',
  message: 'Searching...'
)

app.service 'DataService', ['$http', ($http) ->

  @searchEvents = (eventQuery) ->
    query = []
    if eventQuery.keyword? then query.push "q=#{eventQuery.keyword}"
    if eventQuery.startDate? then query.push "start=#{eventQuery.startDate.toISOString()}"
    if eventQuery.endDate? then query.push "end=#{eventQuery.endDate.toISOString()}"
    query = query.join "&"

    $http.get("searchEvents?#{query}")
      .then (response) ->
        response.data = response.data.map (e) ->
          id:           e._id
          name:         e.name ? null
          description:  e.description ? null
          startTime:    new Date(e.start_time.$date)
          endTime:      if e.end_time? then new Date(e.end_time.$date) else null
          location:     e.location ? null
          locationId:   if e.venue? then e.venue._id ? null else null
          locationLat:  if e.venue? then e.venue.latitude ? null else null
          locationLon:  if e.venue? then e.venue.longitude ? null else null
          ownerName:    if e.owner? then e.owner.name ? null else null
          ownerId:      if e.owner? then e.owner._id ? null else null
        response

  return
]

app.controller 'SearchController', ['$scope', 'DataService', '$log', '$filter', 'ngTableParams', ($scope, dataService, $log, $filter, ngTableParams) ->
  $scope.model =
    eventQuery:
      keyword: null
      startDate: null
      endDate: null
    events: []

  $scope.searchPromise = null

  $scope.datePickerOptions =
    showWeeks: false

  $scope.openStartDate = ($event) ->
    $event.preventDefault()
    $event.stopPropagation()
    $scope.model.startDateOpened = true

  $scope.openEndDate = ($event) ->
    $event.preventDefault()
    $event.stopPropagation()
    $scope.model.endDateOpened = true

  $scope.searchEvents = () ->
    $scope.searchPromise = dataService.searchEvents($scope.model.eventQuery)
    $scope.searchPromise.then(
      (response) ->
        $scope.model.status = null
        $scope.model.events = response.data
      (error) ->
        $log.error "Got error: #{error.statusText}"
        $scope.model.status = "Error performing event query! Reason: #{error.statusText}"
    )

  $scope.checkValidTimeInputs = (elm1, elm2) ->
    start = $scope.model.eventQuery.startDate
    end = $scope.model.eventQuery.endDate

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
    sorting:
      startTime: 'asc'
  ,
    total: 0
    getData: ($defer, params) ->
      orderedData =
        if params.sorting()
          $filter('orderBy')($scope.model.events, params.orderBy())
        else
          $scope.model.events
      $defer.resolve orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count())
  )

  return
]
