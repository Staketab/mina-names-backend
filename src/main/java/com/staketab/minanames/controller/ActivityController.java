package com.staketab.minanames.controller;

import com.staketab.minanames.dto.ActivityDTO;
import com.staketab.minanames.dto.request.BaseRequest;
import com.staketab.minanames.dto.request.sort.ActivitySortColumn;
import com.staketab.minanames.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/{address}")
    @Operation(summary = "getActivities", description = "Get a page of all activities.")
    public Page<ActivityDTO> getActivities(@Valid @ParameterObject BaseRequest request,
                                           @RequestParam @Schema(defaultValue = "TIMESTAMP", allowableValues = {"TIMESTAMP"},
                                                   description = "Select sorting parameter.") ActivitySortColumn sortBy,
                                           @PathVariable String address) {
        return activityService.findAllByPageable(request.withSortColumn(sortBy), address);
    }

    @GetMapping("/domain/{domainName}")
    @Operation(summary = "getActivities", description = "Get a page of all activities.")
    public Page<ActivityDTO> getActivitiesByDomainName(@Valid @ParameterObject BaseRequest request,
                                           @RequestParam @Schema(defaultValue = "TIMESTAMP", allowableValues = {"TIMESTAMP"},
                                                   description = "Select sorting parameter.") ActivitySortColumn sortBy,
                                           @PathVariable String domainName) {
        return activityService.findAllByDomainNameAndPageable(request.withSortColumn(sortBy), domainName);
    }
}
