package com.staketab.minanames.dto.request;

import com.staketab.minanames.dto.request.sort.DbColumnProvider;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class BaseRequest {

    @Parameter(required = true, description = "Queried API page.", schema = @Schema(defaultValue = "0", type = "integer", format = "int32", minimum = "0"))
    @Min(value = 0)
    private int page = 0;
    @Parameter(required = true, description = "Number of queried entries.", schema = @Schema(defaultValue = "20", minimum = "1", maximum = "50", type = "integer", format = "int32"))
    @Min(value = 1)
    @Max(value = 50)
    private int size = 1;
    @Parameter(required = true, schema = @Schema(defaultValue = "DESC", allowableValues = {"ASC", "DESC"}),
            description = "Sorting method: from the lowest element to the highest (ASC) or from the highest element to the lowest (DESC).")
    private OrderBy orderBy;

    @Parameter(hidden = true)
    private String sortByColumn;

    public <T extends DbColumnProvider> BaseRequest withSortColumn(T sortEnum) {
        this.sortByColumn = sortEnum.getDbColumn();
        return this;
    }

    public Pageable buildPageable() {
        Sort sort = getPageSorting();
        return sort == null ? PageRequest.of(page, size) : PageRequest.of(page, size, sort);
    }

    private Sort getPageSorting() {
        return orderBy != null && sortByColumn != null ? buildSort() : null;
    }

    private Sort buildSort() {
        return OrderBy.ASC.equals(orderBy) ? Sort.by(sortByColumn) : Sort.by(sortByColumn).descending();
    }
}
