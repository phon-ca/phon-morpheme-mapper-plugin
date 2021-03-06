package ca.phon.alignedTypesDatabase;

import ca.hedlund.tst.*;

import java.io.*;
import java.util.*;

class TypeLinkedEntry implements Serializable {

	private static final long serialVersionUID = -627133273216532011L;

	private transient TernaryTreeNode<TierInfo> tierNameRef;

	private transient TernaryTreeNodePath tierNamePath;

	private transient Map<TernaryTreeNode<Collection<TypeEntry>>, Integer> linkedTierCounts;

	private transient Map<TernaryTreeNodePath, Integer> linkedNodePaths;

	public TypeLinkedEntry(TernaryTreeNode<TierInfo> tierNameRef) {
		this(tierNameRef, new LinkedHashMap<>());
	}

	public TypeLinkedEntry(TernaryTreeNode<TierInfo> tierNameRef,
	                       Map<TernaryTreeNode<Collection<TypeEntry>>, Integer> linkedTierCounts) {
		super();

		this.tierNameRef = tierNameRef;
		this.linkedTierCounts = linkedTierCounts;
	}

	public String getTierName(TernaryTree<TierInfo> tierDescriptionTree) {
		if(this.tierNameRef == null) {
			if(this.tierNamePath != null) {
				Optional<TernaryTreeNode<TierInfo>> tierInfoOpt =
						tierDescriptionTree.findNode(this.tierNamePath);
				if(tierInfoOpt.isEmpty())
					throw new IllegalStateException("Invalid tier node path");
				this.tierNameRef = tierInfoOpt.get();
			} else {
				throw new IllegalStateException("No tier node path");
			}
		}
		return this.tierNameRef.getPrefix();
	}

	public Map<TernaryTreeNode<Collection<TypeEntry>>, Integer> getLinkedTierCounts(TernaryTree<Collection<TypeEntry>> tree) {
		if(this.linkedTierCounts == null) {
			if(this.linkedNodePaths != null) {
				this.linkedTierCounts = new LinkedHashMap<>();
				for(var path:this.linkedNodePaths.keySet()) {
					final Optional<TernaryTreeNode<Collection<TypeEntry>>> tierNodeOpt =
							tree.findNode(path);
					if(tierNodeOpt.isEmpty())
						throw new IllegalStateException("Invalid value path");
					this.linkedTierCounts.put(tierNodeOpt.get(), this.linkedNodePaths.get(path));
				}
			} else {
				throw new IllegalStateException("No linked values");
			}
		}
		return this.linkedTierCounts;
	}

	public Set<TernaryTreeNode<Collection<TypeEntry>>> getLinkedTierRefs(TernaryTree<Collection<TypeEntry>> tree) {
		return getLinkedTierCounts(tree).keySet();
	}

	public int getLinkedTierCount(TernaryTree<Collection<TypeEntry>> tree,
	                              TernaryTreeNode<Collection<TypeEntry>> linkedNode) {
		var linkedTierCounts= getLinkedTierCounts(tree);
		var storedVal = linkedTierCounts.get(linkedNode);
		if(storedVal != null)
			return storedVal;
		else
			return 0;
	}

	public void addLinkedTier(TernaryTree<Collection<TypeEntry>> tree,
	                          TernaryTreeNode<Collection<TypeEntry>> linkedNode) {
		var linkedTierCounts = getLinkedTierCounts(tree);
		if(!linkedTierCounts.containsKey(linkedNode)) {
			linkedTierCounts.put(linkedNode, 0);
		}
	}

	/**
	 * Increment number for linked tier node, this will add the linked node
	 * to the set if necessary
	 *
	 * @param tree
	 * @param linkedNode
	 */
	public void incrementLinkedTier(TernaryTree<Collection<TypeEntry>> tree,
	                           TernaryTreeNode<Collection<TypeEntry>> linkedNode) {
		var linkedTierCounts = getLinkedTierCounts(tree);
		int newCnt = getLinkedTierCount(tree, linkedNode) + 1;
		linkedTierCounts.put(linkedNode, newCnt);
	}

	/**
	 * Decrement number for linked tier node, removed linked node if value hits zero
	 *
	 * @param tree
	 * @param linkedNode
	 */
	public void decrementLinkedTier(TernaryTree<Collection<TypeEntry>> tree,
			TernaryTreeNode<Collection<TypeEntry>> linkedNode) {
		var linkedTierCounts = getLinkedTierCounts(tree);
		int newCnt = getLinkedTierCount(tree, linkedNode) - 1;
		if(newCnt > 0)
			linkedTierCounts.put(linkedNode, newCnt);
		else
			linkedTierCounts.remove(linkedNode);
	}

	private void readObject(ObjectInputStream oin) throws IOException, ClassNotFoundException {
		this.tierNameRef = null;
		this.linkedTierCounts = null;

		this.tierNamePath = (TernaryTreeNodePath) oin.readObject();
		final int numLinks = oin.readInt();
		this.linkedNodePaths = new LinkedHashMap<>();
		for(int i = 0; i < numLinks; i++) {
			TernaryTreeNodePath linkedPath = (TernaryTreeNodePath) oin.readObject();
			int cnt = (int) oin.readInt();
			this.linkedNodePaths.put(linkedPath, cnt);
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		if(this.tierNameRef != null) {
			out.writeObject(this.tierNameRef.getPath());
		} else if(this.tierNamePath != null) {
			out.writeObject(this.tierNamePath);
		} else {
			throw new IOException("No path to tier name");
		}

		if(this.linkedTierCounts != null) {
			out.writeInt(this.linkedTierCounts.size());
			for(var linkedNode:this.linkedTierCounts.keySet()) {
				out.writeObject(linkedNode.getPath());
				out.writeInt(linkedTierCounts.get(linkedNode));
			}
		} else if(this.linkedNodePaths != null) {
			out.writeInt(this.linkedNodePaths.size());
			for(var linkedPath:linkedNodePaths.keySet()) {
				out.writeObject(linkedPath);
				out.writeInt(linkedNodePaths.get(linkedPath));
			}
		} else {
			out.writeInt(0);
		}
	}

}
