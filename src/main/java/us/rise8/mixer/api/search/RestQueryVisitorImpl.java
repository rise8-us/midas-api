package us.rise8.mixer.api.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.jpa.domain.Specification;

public class RestQueryVisitorImpl<T> extends RestQueryBaseVisitor<Specification<T>> {

    private final Pattern val = Pattern.compile("^(\\*?)(.+?)(\\*?)$");

    @Override
    public Specification<T> visitOpQuery(RestQueryParser.OpQueryContext ctx) {
        Specification<T> left = visit(ctx.left);
        Specification<T> right = visit(ctx.right);
        String op = ctx.logicalOp.getText();

        if ("OR".equals(op)) {
            return left.or(right);
        }
        return left.and(right);
    }

    @Override
    public Specification<T> visitPriorityQuery(RestQueryParser.PriorityQueryContext ctx) {
        return visit(ctx.query());
    }

    @Override
    public Specification<T> visitAtomQuery(RestQueryParser.AtomQueryContext ctx) {
        return visit(ctx.criteria());
    }

    @Override
    public Specification<T> visitInput(RestQueryParser.InputContext ctx) {
        return visit(ctx.query());
    }

    @Override
    public Specification<T> visitCriteria(RestQueryParser.CriteriaContext ctx) {
        String key = ctx.key().getText();
        String op = ctx.op().getText();
        String value = ctx.value().getText();
        if (ctx.value().STRING() != null) {
            value = value
                    .replace("^\"", "")
                    .replace("\"$", "")
                    .replace("^'", "")
                    .replace("'$", "")
                    .replace("\\\"", "\"")
                    .replace("\\'", "'")
                    .trim();
        }
        Matcher matcher = val.matcher(value);
        matcher.find();
        SearchCriteria criteria = new SearchCriteria(key, op, matcher.group(1), matcher.group(2), matcher.group(3));
        return new SpecificationImpl<>(criteria);
    }

}
