package tech.pegasys.errorpronechecks;

import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.SeverityLevel.SUGGESTION;
import static com.google.errorprone.matchers.Matchers.contains;
import static com.sun.source.tree.Tree.Kind.NULL_LITERAL;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;

/*
 * This is reworked from an example found at:
 * https://github.com/google/error-prone/wiki/Writing-a-check
 */

@AutoService(BugChecker.class) // the service descriptor
@BugPattern(
  name = "DoNotReturnNullOptionals",
  summary = "Do not return null optionals.",
  category = JDK,
  severity = SUGGESTION
)
public class DoNotReturnNullOptionals extends BugChecker implements MethodTreeMatcher {

  private static class ReturnNullMatcher implements Matcher<Tree> {

    @Override
    public boolean matches(final Tree tree, final VisitorState state) {
      if ((tree instanceof ReturnTree) && (((ReturnTree) tree).getExpression() != null)) {
        return ((ReturnTree) tree).getExpression().getKind() == NULL_LITERAL;
      }
      return false;
    }
  }

  private static final Matcher<Tree> RETURN_NULL = new ReturnNullMatcher();
  private static final Matcher<Tree> CONTAINS_RETURN_NULL = contains(RETURN_NULL);

  @Override
  public Description matchMethod(final MethodTree tree, final VisitorState state) {
    if ((tree.getReturnType() == null)
        || !tree.getReturnType().toString().startsWith("Optional<")
        || (tree.getBody() == null)
        || (!CONTAINS_RETURN_NULL.matches(tree.getBody(), state))) {
      return Description.NO_MATCH;
    }
    return describeMatch(tree);
  }
}