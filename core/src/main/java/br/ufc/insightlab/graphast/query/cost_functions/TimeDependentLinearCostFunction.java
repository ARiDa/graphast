package br.ufc.insightlab.graphast.query.cost_functions;

import br.ufc.insightlab.graphast.exceptions.ComponentNotFoundException;
import br.ufc.insightlab.graphast.model.Edge;
import br.ufc.insightlab.graphast.model.components.cost_list_components.CostListEdgeComponent;

import java.util.List;

public class TimeDependentLinearCostFunction extends TimeDependentCostFunction {

	public TimeDependentLinearCostFunction(int hour, int min) {
		this.setTime(hour, min);
	}
	
	private double getLinearInterpolation(double n1, double n2, double x) {
		return n1 + x * (n2 - n1);
	}
	
	public double getLinearCost(List<Double> costList) {
		double relativePosition = this.getRelativePositionInCostList(costList.size());
		if (relativePosition == -1) return -1;

		int index = (int) Math.floor(relativePosition);
		
		double x = relativePosition - index;
		double n1 = costList.get(index);
		double n2 = (index + 1 < costList.size()) ? costList.get(index + 1) : costList.get(0);
		return getLinearInterpolation(n1,  n2, x);
	}

	@Override
	public double getCost(Edge e) throws RuntimeException {
		CostListEdgeComponent component = e.getComponent(CostListEdgeComponent.class);
		if (component == null)
			throw new ComponentNotFoundException(CostListEdgeComponent.class);
		return getLinearCost(component.getCostList());
	}


}
