package io.ten1010.coaster.auth.web;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public class ListOptions {

    public static final String PARAM_KEY_PAGE = "page";
    public static final String PARAM_KEY_LIMIT = "limit";
    public static final String PARAM_KEY_FIND_BY = "findBy";
    public static final String PARAM_KEY_QUERY = "query";

    public static ListOptions parse(Map<String, String[]> params) {
        ListOptions options = new ListOptions();
        parse(params, options);

        return options;
    }

    public static void parse(Map<String, String[]> params, ListOptions options) {
        if (params.containsKey(PARAM_KEY_PAGE)) {
            String[] values = params.get(PARAM_KEY_PAGE);
            if (values.length != 1) {
                throw WebPropertyExceptionBuilder.parameter()
                        .setPropertyPath("/" + PARAM_KEY_PAGE)
                        .setMessage(WebPropertyExceptionBuilder.MSG_SINGLE_VALUE_ALLOWED_ONLY)
                        .build();
            }
            options.page = parseInt(values[0], PARAM_KEY_PAGE);
        }
        if (params.containsKey(PARAM_KEY_LIMIT)) {
            String[] values = params.get(PARAM_KEY_LIMIT);
            if (values.length != 1) {
                throw WebPropertyExceptionBuilder.parameter()
                        .setPropertyPath("/" + PARAM_KEY_LIMIT)
                        .setMessage(WebPropertyExceptionBuilder.MSG_SINGLE_VALUE_ALLOWED_ONLY)
                        .build();
            }
            options.limit = parseInt(values[0], PARAM_KEY_LIMIT);
        }
        if (options.page != null && options.limit == null) {
            throw WebPropertyExceptionBuilder.parameter()
                    .setPropertyPath("/" + PARAM_KEY_LIMIT)
                    .setMessage("Can not be null when page exist")
                    .build();
        }
        if (options.page == null && options.limit != null) {
            throw WebPropertyExceptionBuilder.parameter()
                    .setPropertyPath("/" + PARAM_KEY_PAGE)
                    .setMessage("Can not be null when limit exist")
                    .build();
        }
        if (params.containsKey(PARAM_KEY_FIND_BY)) {
            String[] values = params.get(PARAM_KEY_FIND_BY);
            if (values.length != 1) {
                throw WebPropertyExceptionBuilder.parameter()
                        .setPropertyPath("/" + PARAM_KEY_FIND_BY)
                        .setMessage(WebPropertyExceptionBuilder.MSG_SINGLE_VALUE_ALLOWED_ONLY)
                        .build();
            }
            options.findBy = values[0];
        }
        if (params.containsKey(PARAM_KEY_QUERY)) {
            String[] values = params.get(PARAM_KEY_QUERY);
            if (values.length != 1) {
                throw WebPropertyExceptionBuilder.parameter()
                        .setPropertyPath("/" + PARAM_KEY_QUERY)
                        .setMessage(WebPropertyExceptionBuilder.MSG_SINGLE_VALUE_ALLOWED_ONLY)
                        .build();
            }
            options.query = values[0];
            if (options.findBy == null) {
                throw WebPropertyExceptionBuilder.parameter()
                        .setPropertyPath("/" + PARAM_KEY_FIND_BY)
                        .setMessage("Can not be null when query exist")
                        .build();
            }
        }
    }

    private static int parseInt(String str, String paramKey) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw WebPropertyExceptionBuilder.parameter()
                    .setPropertyPath("/" + paramKey)
                    .setMessage("Integer value allowed only")
                    .build();
        }
    }

    @ToString.Include
    private Integer page;
    @ToString.Include
    private Integer limit;
    @ToString.Include
    private String findBy;
    @ToString.Include
    private String query;

    public Optional<Integer> getPage() {
        return Optional.ofNullable(this.page);
    }

    public Optional<Integer> getLimit() {
        return Optional.ofNullable(this.limit);
    }

    public Pageable getPageable() {
        Pageable pageable = Pageable.unpaged();
        if (getPage().isPresent()) {
            if (getLimit().isEmpty()) {
                throw new IllegalArgumentException();
            }
            pageable = PageRequest.of(getPage().get(), getLimit().get());
        }

        return pageable;
    }

    public Optional<String> getFindBy() {
        return Optional.ofNullable(this.findBy);
    }

    public Optional<String> getQuery() {
        return Optional.ofNullable(this.query);
    }

}
