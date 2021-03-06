/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.eigenbase.sql.validate;

import org.eigenbase.reltype.*;
import org.eigenbase.resource.*;
import org.eigenbase.sql.*;
import org.eigenbase.util.*;

/**
 * Namespace based upon a set operation (UNION, INTERSECT, EXCEPT).
 */
public class SetopNamespace extends AbstractNamespace {
  //~ Instance fields --------------------------------------------------------

  private final SqlCall call;

  //~ Constructors -----------------------------------------------------------

  /**
   * Creates a <code>SetopNamespace</code>.
   *
   * @param validator     Validator
   * @param call          Call to set operator
   * @param enclosingNode Enclosing node
   */
  protected SetopNamespace(
      SqlValidatorImpl validator,
      SqlCall call,
      SqlNode enclosingNode) {
    super(validator, enclosingNode);
    this.call = call;
  }

  //~ Methods ----------------------------------------------------------------

  public SqlNode getNode() {
    return call;
  }

  public RelDataType validateImpl() {
    switch (call.getKind()) {
    case UNION:
    case INTERSECT:
    case EXCEPT:
      final SqlValidatorScope scope = validator.scopes.get(call);
      for (SqlNode operand : call.operands) {
        if (!(operand.isA(SqlKind.QUERY))) {
          throw validator.newValidationError(
              operand,
              EigenbaseResource.instance().NeedQueryOp.ex(
                  operand.toString()));
        }
        validator.validateQuery(operand, scope);
      }
      return call.getOperator().validateOperands(
          validator,
          scope,
          call);
    default:
      throw Util.newInternal("Not a query: " + call.getKind());
    }
  }
}

// End SetopNamespace.java
